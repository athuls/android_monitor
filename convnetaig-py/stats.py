import torch
import convnet_aig
import time

def timer(N, module, layers):
  data = torch.empty(N)
  for i in range(N):
    start_time = time.time()
    module(torch.rand(1, 3, 224, 224), layers)
    end_time = time.time()
    data[i] = end_time - start_time
  return f"{data.mean()*1000:3.2f}ms (std {data.std()*1000:3.2f}ms)"

model = convnet_aig.ResNet50_ImageNet()
model.eval()
script = torch.jit.script(model)

def onesFromTo(start, end):
    return torch.tensor([False] * start + [True] * (end - start) + [False] * (16 - end))

# for start in (0, 4, 8, 12, 0):
#     end = start + 4
#     timer(100, script, onesFromTo(start, end))
#     print(f"[{start:02d}, {end:02d}):", timer(900, script, onesFromTo(start, end)))
script.save("imagenet.pt")