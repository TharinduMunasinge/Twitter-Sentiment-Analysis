library("maxent")
library("tm")
load("positiveTweets.RData")
load("negativeTweets.RData")
load("neutralTweets.RData")
load("testData.RData")

source("preprocessTweets.R")
source("semanticAnalysis.R")

trainData <- rbind(rbind(positiveTweets,negativeTweets),neutralTweets)
data <- rbind(trainData,testData)
data<-trainData
holdOut <-nrow(testData)
size <- nrow(data)
remain <- size-holdOut

indices <- sample(1:size, size, replace=FALSE)
sample <- data[indices,]
vector <- VectorSource(as.vector(sample$text))
corpus <- Corpus(vector)
print("corpus done")
matrix <- TermDocumentMatrix(corpus, control=list(weighting = weightTfIdf,language = "english",tolower = TRUE,stopwords = TRUE,removeNumbers = TRUE,removePunctuation = TRUE,stripWhitespace = TRUE))
print("matrix done")
model <- maxent(matrix[,1:remain],sample$sentiment[1:remain],set_heldout=holdOut,l1_regularizer=0.1, l2_regularizer=1)
print("model done")
index <- remain + 1
results <- predict(model, matrix[,index:size])
compare <- data.frame(actual=testData$sentiment,prediction=results[,1])
print("positive accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="positive",])/nrow(compare[compare$actual=="positive",]))
print("negative accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="negative",])/nrow(compare[compare$actual=="negative",]))
print("neutral accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="neutral",])/nrow(compare[compare$actual=="neutral",]))
