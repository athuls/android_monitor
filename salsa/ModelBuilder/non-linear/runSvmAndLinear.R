library(hydroGOF)
library(e1071)


for(i in 100:1000) {
	if(i %% 100) {
		next
	}

	avgSvm<-0
	avgLinear<-0
	avgError<-0
	runs<-5	

	for(j in 1:runs) {
		trainCommand<-sprintf("python simulator_non_linear.py %d > simtrain.csv", i)
		testCommand<-sprintf("python simulator_non_linear.py %d > simtest.csv", i)
		system(trainCommand)
		system(testCommand)
		simtrain <- read.csv("simtrain.csv")
		simtest <- read.csv("simtest.csv")
		
		model<-lm(V2~V1,simtrain)
		modelsvm<-svm(V2~V1,simtrain)
		#summary(model)
		#summary(modelsvm)
		predY<-predict(model,simtest)
		predsvmY<-predict(modelsvm,simtest)
		rmse_linear<-rmse(predY,simtest$V2)
		rmse_svm<-rmse(predsvmY,simtest$V2)
		
		avgLinear<-avgLinear + rmse_linear
		avgSvm<-avgSvm + rmse_svm
		print(i)
		print("Linear:")
		print(rmse_linear)
		print("SVM:")
		print(rmse_svm)
		print((rmse_linear - rmse_svm)/rmse_linear)
	}
	
	avgLinear<-avgLinear/runs
	avgSvm<-avgSvm/runs
	outAvg<-sprintf("AvgLinear:%f,AvgSVM:%f", avgLinear, avgSvm)
	print(outAvg)
}
