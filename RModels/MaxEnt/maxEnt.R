library("maxent")
library("tm")

load("fullProcessedData.RData")
source("preprocessTweets.R")
source("semanticAnalysis.R")


fullData$tweet <- as.character(fullData$tweet)
fullData$senti[fullData$senti==0] <- "negative"
fullData$senti[fullData$senti==4] <- "positive"
fullData$senti[fullData$senti==2] <- "neutral"

fullData$emoticonScore <- 0
fullData$verbScore <- 0
fullData$adjectiveScore <- 0


#indices <- sample(1:997, 997, replace=FALSE)
#sample <- data[indices,]
sample <-fullData[1:997,] 
vector <- VectorSource(as.vector(fullData$tweet[1:997]))
corpus <- Corpus(vector)
print("corpus done")
matrix <- TermDocumentMatrix(corpus, control=list(weighting = weightTfIdf,language = "english",tolower = TRUE,stopwords = TRUE,removeNumbers = TRUE,removePunctuation = TRUE,stripWhitespace = TRUE))
print("matrix done")
model <- maxent(matrix[,1:699],sample$senti[1:699],set_heldout=20,l1_regularizer=0.7, l2_regularizer=0,use_sgd=TRUE)
print("model done")

results <- predict(model, matrix[,700:997])
compare <- data.frame(actual=fullData$senti[700:997],prediction=results[,1])
print("positive accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="positive",])/nrow(compare[compare$actual=="positive",]))
print("negative accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="negative",])/nrow(compare[compare$actual=="negative",]))
print("neutral accuracy")
print(nrow(compare[compare$actual==compare$prediction & compare$actual=="neutral",])/nrow(compare[compare$actual=="neutral",]))
