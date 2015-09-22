testset <- read.csv("testset_java_gen.csv")
sent <- read.csv("test_data.csv", header=FALSE)$V1

sent=sent[1:50,]
testset=testset[1:50,]

library(neuralnet)
load("net.RData")
results <- compute(net, testset)
correct = (round(results$net.result) == sent)
table(correct)