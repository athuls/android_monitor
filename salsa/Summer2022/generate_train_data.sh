#!/bin/sh

inp_arr=( "$@" )

#Add training data to output file in append mode
raw_data_dir="$1"
train_file="${@: -1}"

arg_c=1
num_inputs=$#

for filename in "$raw_data_dir"/*; do
	python3 analyze_operator.py $filename $train_file True True
done 

#for var in "$@"
#do
#	if [[ "$num_inputs" == "$arg_c" ]]
#	then 
#		break
#	fi
#	let "arg_c=arg_c+1"
#done
