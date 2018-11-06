#!/bin/sh

if [ $# != 3 ]; then
	echo "Arguments should be: config file name, data input file, end index of X in training"
	exit 1
fi

config_file=$1
input_file=$2
# out_folds=$3
x_end_index=$3

sed -i "/train_input/s/ = .*/ = .\/$input_file/" $config_file
sed -i "/x_end_index/s/ = .*/ = $x_end_index/" $config_file

out_folds_arr=(3 5 10 20)

for out_folds in "${out_folds_arr[@]}"
do
	sed -i "/outer_folds/s/ = .*/ = $out_folds/" $config_file	
	out_file_1="new_exsort_rfr_out_$out_folds""_1"
	out_file_2="new_exsort_rfr_out_$out_folds""_2"
	out_file_3="new_exsort_rfr_out_$out_folds""_3"
	python model.py $config_file > $out_file_1 &&
	python model.py $config_file > $out_file_2 &&
	python model.py $config_file > $out_file_3 &&
	echo "Done with $out_folds" 
done
