#!/bin/bash
#SBATCH --gpus=2

module load python/3
export PYTHONPATH=/scratch/users/$USER/pytorch-venv:${PYTHONPATH}
python3 -u train_img.py --resume runs/my_experiment/checkpoint.pth.tar /scratch/users/$USER/trimmed_imagenet
