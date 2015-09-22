
require(nnet)
require(caret)
trainset <- read.csv("trainset_java_gen.csv")
sent1 <- read.csv("train_data.csv")
trainset <- cbind(trainset, sent1)



testset <- read.csv("testset_java_gen.csv")
sent2 <- read.csv("test_data.csv")
testset=cbind(testset,sent2)
set.seed(42)

model <- train(sent ~., trainset, method='nnet', linout=TRUE, trace = FALSE,
               #Grid of tuning parameters to try:
               tuneGrid=expand.grid(.size=c(1,5,10),.decay=c(0,0.001,0.1))) 

res <- predict(model, testset)


print(cbind(res,sent2))
for (i in 1:length(res)){
  if(res[i]<=2.01 && res[i]>=1.99 )
    res[i]=2
  else if((res[i]>2.01))
    res[i]=4
  else if(res[i]<1.99)
    res[i]=0
}

print(res)

table(res==sent2)


