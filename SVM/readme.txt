The readme file
**************************************************************
The command line for the SVM

svm-train.exe -t {0,1,2,3} training.new

-t is the parameter for the different kernel methd, for instance: 
if command is: svm-train.exe -t 0 training.new
it means we choose the linear kernel mathod.
Then the command should be:

svm-predict.exe validation.new training.new.model validation.out

then we can get the accuracy of the SVM
