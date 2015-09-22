
library('nnet')

trainset <- read.csv("trainset_java_gen.csv")
sent1 <- read.csv("train_data.csv")
trainset <- cbind(trainset, sent1)

testset <- read.csv("testset_java_gen.csv")
sent2 <- read.csv("test_data.csv")
testset=cbind(testset,sent2)
set.seed(42)
res <- nnet(sent ~.,
            data=trainset,
            size=20, linout=TRUE, hidden=10, skip=FALSE, MaxNWts=10000, trace=FALSE, maxit=10000);

#correct = (round(results$net.result) == sent)
res=predict(res, newdata=testset)


#Examine results

plot(testset$sent)
lines(res, col=2)

print(cbind(res,sent2))
for (i in 1:length(res)){
  if(res[i]<=2.1 && res[i]>=1.9 )
    res[i]=2
  else if((res[i]>2.1 ))
    res[i]=4
  else if(res[i]<1.9)
    res[i]=0
}

print(res)

table(res==sent2)