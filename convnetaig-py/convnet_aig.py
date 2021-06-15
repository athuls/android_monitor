'''ConvNet-AIG in PyTorch.

Residual Network is from the original ResNet paper:
[1] Kaiming He, Xiangyu Zhang, Shaoqing Ren, Jian Sun
    Deep Residual Learning for Image Recognition. arXiv:1512.03385

Adaptive Inference Graphs is from the original ConvNet-AIG paper:
[2] Andreas Veit, Serge Belognie
    Convolutional Networks with Adaptive Inference Graphs. ECCV 2018

'''
import torch
import torch.nn as nn
import torch.nn.functional as F
import math
import numpy as np
from typing import Optional
from torch.autograd import Variable

from gumbelmodule import GumbleSoftmax


def conv3x3(in_planes, out_planes, stride=1):
    return nn.Conv2d(in_planes, out_planes, kernel_size=3, stride=stride, padding=1, bias=False)


class Sequential_ext(nn.Module):
    """A Sequential container extended to also propagate the gating information
    that is needed in the target rate loss.
    """

    def __init__(self, *args):
        super(Sequential_ext, self).__init__()
        if len(args) == 1 and isinstance(args[0], OrderedDict):
            for key, module in args[0].items():
                self.add_module(key, module)
        else:
            for idx, module in enumerate(args):
                self.add_module(str(idx), module)

    def __getitem__(self, idx):
        if not (-len(self) <= idx < len(self)):
            raise IndexError('index {} is out of range'.format(idx))
        if idx < 0:
            idx += len(self)
        it = iter(self.modules())
        for i in range(idx):
            next(it)
        return next(it)

    def __len__(self):
        return len(self._modules)

    def forward(self, input, layers: Optional[torch.Tensor] = None):
        gate_activations = []
        for i, module in enumerate(self.children()):
            input, gate_activation = module(input, None if layers is None else layers[i])
            gate_activations.append(gate_activation)
        if layers is not None: assert i == len(layers) - 1
        return input, gate_activations


class BasicBlock(nn.Module):
    expansion = 1

    blocks_created = 0

    def __init__(self, in_planes, planes, stride=1):
        super(BasicBlock, self).__init__()
        self.conv1 = nn.Conv2d(in_planes, planes, kernel_size=3, stride=stride, padding=1, bias=False)
        self.bn1 = nn.BatchNorm2d(planes)
        self.conv2 = nn.Conv2d(planes, planes, kernel_size=3, stride=1, padding=1, bias=False)
        self.bn2 = nn.BatchNorm2d(planes)

        self.shortcut = nn.Sequential()
        if stride != 1 or in_planes != self.expansion*planes:
            self.shortcut = nn.Sequential(
                nn.Conv2d(in_planes, self.expansion*planes, kernel_size=1, stride=stride, bias=False),
                nn.BatchNorm2d(self.expansion*planes)
            )

        # Gate layers
        self.fc1 = nn.Conv2d(in_planes, 16, kernel_size=1)
        self.fc1bn = nn.BatchNorm2d(16)
        self.fc2 = nn.Conv2d(16, 2, kernel_size=1)
        # initialize the bias of the last fc for 
        # initial opening rate of the gate of about 85%
        self.fc2.bias.data[0] = 0.1
        self.fc2.bias.data[1] = 2
        self.gs = GumbleSoftmax()
        self.gs.cuda()

        self.block_id = BasicBlock.blocks_created
        BasicBlock.blocks_created += 1

    def forward(self, x, force_execute: Optional[torch.Tensor] = None):
        # Compute relevance score
        w = F.avg_pool2d(x, x.size(2))
        w = F.relu(self.fc1bn(self.fc1(w)))
        w = self.fc2(w)
        # Sample from Gumble Module
        w = self.gs(w, force_hard=True)

        should_do = w[:,1]
        if force_execute is not None:
            should_do = force_execute

        out = self.shortcut(x)
        if self.training or should_do:
          x = F.relu(self.bn1(self.conv1(x)))
          x = self.bn2(self.conv2(x))
          out += x

        out = F.relu(out)
        # Return output of layer and the value of the gate
        # The value of the gate will be used in the target rate loss
        return out, should_do


class Bottleneck(nn.Module):
    expansion = 4

    def __init__(self, in_planes, planes, stride=1):
        super(Bottleneck, self).__init__()
        self.conv1 = nn.Conv2d(in_planes, planes, kernel_size=1, bias=False)
        self.bn1 = nn.BatchNorm2d(planes)
        self.conv2 = nn.Conv2d(planes, planes, kernel_size=3, stride=stride, padding=1, bias=False)
        self.bn2 = nn.BatchNorm2d(planes)
        self.conv3 = nn.Conv2d(planes, self.expansion*planes, kernel_size=1, bias=False)
        self.bn3 = nn.BatchNorm2d(self.expansion*planes)

        self.shortcut = nn.Sequential()
        if stride != 1 or in_planes != self.expansion*planes:
            self.shortcut = nn.Sequential(
                nn.Conv2d(in_planes, self.expansion*planes, kernel_size=1, stride=stride, bias=False),
                nn.BatchNorm2d(self.expansion*planes)
            )

        # Gate layers
        self.fc1 = nn.Conv2d(in_planes, 16, kernel_size=1)
        self.fc1bn = nn.BatchNorm2d(16)
        self.fc2 = nn.Conv2d(16, 2, kernel_size=1)
        # initialize the bias of the last fc for 
        # initial opening rate of the gate of about 85%
        self.fc2.bias.data[0] = 0.1
        self.fc2.bias.data[1] = 2

        self.gs = GumbleSoftmax()
        self.gs.cuda()

    def forward(self, x, force_execute: Optional[torch.Tensor] = None):
        assert force_execute is None
        # Compute relevance score
        w = F.avg_pool2d(x, x.size(2))
        w = F.relu(self.fc1bn(self.fc1(w)))
        w = self.fc2(w)
        # Sample from Gumble Module
        w = self.gs(w, force_hard=True)

        should_do = w[:,1]

        out = self.shortcut(x)
        x = F.relu(self.bn1(self.conv1(x)), inplace=True)
        x = F.relu(self.bn2(self.conv2(x)), inplace=True)
        x = self.bn3(self.conv3(x))
        out = out + x

        out = F.relu(out, inplace=True)
        # Return output of layer and the value of the gate
        # The value of the gate will be used in the target rate loss
        return out, should_do

    
class ResNet_ImageNet(nn.Module):

    def __init__(self, block, layers, num_classes=1000):
        self.in_planes = 64
        super(ResNet_ImageNet, self).__init__()
        self.conv1 = nn.Conv2d(3, 64, kernel_size=7, stride=2, padding=3,
                               bias=False)
        self.bn1 = nn.BatchNorm2d(64)
        self.relu = nn.ReLU(inplace=True)
        self.maxpool = nn.MaxPool2d(kernel_size=3, stride=2, padding=1)
        self.layer1 = self._make_layer(block, 64, layers[0])
        self.layer2 = self._make_layer(block, 128, layers[1], stride=2)
        self.layer3 = self._make_layer(block, 256, layers[2], stride=2)
        self.layer4 = self._make_layer(block, 512, layers[3], stride=2)
        self.avgpool = nn.AvgPool2d(7, stride=1)
        self.linear = nn.Linear(512 * block.expansion, num_classes)

        self.layercount = layers.cumsum(0)

        for k, m in self.named_modules():
            if isinstance(m, nn.Conv2d):
                n = m.kernel_size[0] * m.kernel_size[1] * m.out_channels
                m.weight.data.normal_(0, math.sqrt(2. / n))
                if 'fc2' in str(k):
                    # Initialize last layer of gate with low variance
                    m.weight.data.normal_(0, 0.001)
            elif isinstance(m, nn.BatchNorm2d):
                m.weight.data.fill_(1)
                m.bias.data.zero_()

    def _make_layer(self, block, planes, num_blocks, stride=1):
        strides = [stride] + [1]*(num_blocks-1)
        layers = []
        for stride in strides:
            layers.append(block(self.in_planes, planes, stride))
            self.in_planes = planes * block.expansion
        return Sequential_ext(*layers)

    def forward(self, out, layers: Optional[torch.Tensor] = None):
        if layers is not None: assert len(layers) == self.layercount[3]
        gate_activations = []
        out = self.relu(self.bn1(self.conv1(out)))
        out = self.maxpool(out)
        out, a = self.layer1(out, None if layers is None else layers[:self.layercount[0]])
        gate_activations.extend(a)
        out, a = self.layer2(out, None if layers is None else layers[self.layercount[0]:self.layercount[1]])
        gate_activations.extend(a)
        out, a = self.layer3(out, None if layers is None else layers[self.layercount[1]:self.layercount[2]])
        gate_activations.extend(a)
        out, a = self.layer4(out, None if layers is None else layers[self.layercount[2]:])
        gate_activations.extend(a)
        out = self.avgpool(out)
        out = out.view(out.size(0), -1)
        out = self.linear(out)
        return out, gate_activations

class ResNet_cifar(nn.Module):
    def __init__(self, block, num_blocks, num_classes=10):
        super(ResNet_cifar, self).__init__()
        self.in_planes = 16

        self.conv1 = conv3x3(3,16)
        self.bn1 = nn.BatchNorm2d(16)
        self.layer1 = self._make_layer(block, 16, num_blocks[0], stride=1)
        self.layer2 = self._make_layer(block, 32, num_blocks[1], stride=2)
        self.layer3 = self._make_layer(block, 64, num_blocks[2], stride=2)
        self.linear = nn.Linear(64*block.expansion, num_classes)

        self.layercount = num_blocks.cumsum(0)

        for k, m in self.named_modules():
            if isinstance(m, nn.Conv2d):
                if 'fc2' in str(k):
                    # Initialize last layer of gate with low variance
                    m.weight.data.normal_(0, 0.001)

    def _make_layer(self, block, planes, num_blocks, stride):
        strides = [stride] + [1]*(num_blocks-1)
        layers = []
        for stride in strides:
            layers.append(block(self.in_planes, planes, stride))
            self.in_planes = planes * block.expansion
        return Sequential_ext(*layers)

    def forward(self, x, layers: Optional[torch.Tensor] = None):
        if layers is not None: assert len(layers) == self.layercount[2]
        gate_activations = []
        out = F.relu(self.bn1(self.conv1(x)))
        out, a = self.layer1(out, None if layers is None else layers[:self.layercount[0]])
        gate_activations.extend(a)
        out, a = self.layer2(out, None if layers is None else layers[self.layercount[0]:self.layercount[1]])
        gate_activations.extend(a)
        out, a = self.layer3(out, None if layers is None else layers[self.layercount[1]:])
        gate_activations.extend(a)
        out = F.avg_pool2d(out, 8)
        out = out.view(out.size(0), -1)
        out = self.linear(out)
        return out, gate_activations

def ResNet110_cifar(nclass=10):
    return ResNet_cifar(BasicBlock, torch.tensor([18,18,18]), num_classes=nclass)

def ResNet50_ImageNet():
    return ResNet_ImageNet(Bottleneck, torch.tensor([3,4,6,3]))

def ResNet101_ImageNet():
    return ResNet_ImageNet(Bottleneck, [3,4,23,3])

def ResNet152_ImageNet():
    return ResNet_ImageNet(Bottleneck, [3,8,36,3])


class ActivationAccum():
    def __init__(self, epoch):
        self.numblocks = [18,18,18]
        self.gates = {i: 0 for i in range(np.sum(self.numblocks))}
        self.classes = {i: 0 for i in range(10)}
        self.numbatches = 0
        self.epoch = epoch

        if self.epoch % 25 == 0:
            self.heatmap = torch.FloatTensor(len(self.classes), len(self.gates))
            self.heatmap[:, :] = 0

    def accumulate(self, actives, targets):
        for j, act in enumerate(actives):
            self.gates[j] += torch.sum(act)

            if self.epoch % 25 == 0:
                for k in range(10):
                    self.classes[k] += torch.sum(act[targets==k])
                    self.heatmap[k, j] += torch.sum(act[targets==k]).item()

            self.numbatches += 1
            
    def getoutput(self):
        if self.epoch % 25 == 0:
            return([{k: self.gates[k].item() / 10000 for k in self.gates},
                {k: self.classes[k].item() / 1000 / np.sum(self.numblocks) for k in self.classes},
                self.heatmap.cpu().numpy() / 1000])
        else:
            return([{k: self.gates[k].item() / 10000 for k in self.gates}])


class ActivationAccum_img():
    def __init__(self, epoch):
        self.numblocks = [3,4,23,3]
        self.gates = {i: 0 for i in range(np.sum(self.numblocks))}
        self.classes = {i: 0 for i in range(1000)}
        self.numbatches = 0
        self.epoch = epoch

        if epoch in [30, 60, 99,149]:
            self.heatmap = torch.FloatTensor(len(self.classes), len(self.gates))
            self.heatmap[:, :] = 0

    def accumulate(self, actives, targets, target_rates):
        for j, act in enumerate(actives):
            if target_rates[j] < 1:
                self.gates[j] += torch.sum(act)
            else:
                self.gates[j] += targets.size(0)

            if self.epoch in [30, 60, 99, 149]:
                for k in range(1000):
                    if target_rates[j] < 1:
                        self.classes[k] += torch.sum(act[targets==k]).item()
                        self.heatmap[k, j] += torch.sum(act[targets==k]).item()
                    else:
                        self.classes[k] += torch.sum(targets==k).item()
                        self.heatmap[k, j] += torch.sum(targets==k).item()

            self.numbatches += 1
    def getoutput(self):
        for k in list(self.gates.keys()):
            if type(self.gates[k]) != int:
                self.gates[k] = self.gates[k].item()
        
        if self.epoch in [30, 60, 99, 149]:
            return([{k: self.gates[k] / 50000 for k in self.gates},
                   {k: self.classes[k] / 50 / np.sum(self.numblocks) for k in self.classes},
                   self.heatmap.cpu().numpy() / 50])
        else:
            return([{k: self.gates[k] / 50000 for k in self.gates}])
