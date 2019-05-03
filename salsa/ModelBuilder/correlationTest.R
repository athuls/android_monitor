df <- read.csv("debug/overall_nums_display_neg_coeff_debug_full_clean_idleremove.txt", header=FALSE)
shapiro.test(df$V25)
res <- cor.test(df$V8, df$V25, method="pearson")



-----------------------------------------------
Gnuplot
>> binwidth=5
>> bin(x,width)=width*floor(x/width)
>> plot 'var_workload/overall_var_wld_numbers_normal_distr.txt' using (bin($7,binwidth)):(1.0) smooth freq with boxes

