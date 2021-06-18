#!/bin/bash
#SBATCH --time=04:00:00
#SBATCH --gres=gpu:V100:2

module load anaconda/3
source activate python-env
python3 -u train_img.py --expname 0.6 --resume runs/0.6/checkpoint.pth.tar --batch-size 64 --target 0.6 /scratch/trimmed_imagenet
