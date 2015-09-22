source("semanticAnalysis.R")

load("Data/testData.RData")
load("Data/positiveWordBag.RData")
load("Data/negativeWordBag.RData")
load("Data/neutralWordBag.RData")


testData$mp <- 0
testData$mng <- 0
testData$mne <- 0
testData$bp <- 0
testData$bng <- 0
testData$bne <- 0
testData$multinomial <- "null"
testData$bernuli <- "null"
tweetCount <- 1000

#testData <- testData[1:5,]


for (i in 1:nrow(testData)){
	
	write.csv(testData$text[i],file="tweet")
	tweetPOSTree <- posTagger("tweet")
	tweetPOSTree$Token <- as.character(tweetPOSTree$Token)
	multinomialPos <- 0
	multinomialNeg <- 0
	multinomialNeu<- 0
	
	bernuliPos <- 0
	bernuliNeg <- 0
	bernuliNeu<- 0
	
	
	totalPositiveWords <- sum(positiveWordBag$count)
	totalNegativeWords <- sum(negativeWordBag$count)
	totalNeutralWords <- sum(neutralWordBag$count)
	
	for (j in 1:nrow(tweetPOSTree)){
		token <- tweetPOSTree$Token[j]
		
		if (token %in% positiveWordBag$token){
			index <- match(token,positiveWordBag$token)
			multinomialPos <- multinomialPos + log(positiveWordBag$count[index]/totalPositiveWords)
			bernuliPos <- bernuliPos + log(positiveWordBag$occurance[index]/tweetCount)
		}
		
		if (token %in% negativeWordBag$token){
			index <- match(token,negativeWordBag$token)
			multinomialNeg <- multinomialNeg + log(negativeWordBag$count[index]/totalNegativeWords)
			bernuliNeg <- bernuliNeg + log(negativeWordBag$occurance[index]/tweetCount)
		}
		
		if (token %in% neutralWordBag$token){
			index <- match(token,neutralWordBag$token)
			multinomialNeu <- multinomialNeu + log(neutralWordBag$count[index]/totalNeutralWords)
			bernuliNeu <- bernuliNeu + log(neutralWordBag$occurance[index]/tweetCount)
		}
			
	}
	
	testData$mp[i] <- multinomialPos
	testData$mng[i] <- multinomialNeg
	testData$mne[i] <- multinomialNeu
	testData$bp[i] <- bernuliPos
	testData$bng[i] <- bernuliNeg
	testData$bne[i] <- bernuliNeu
	
	maxScore <- max(multinomialPos,multinomialNeg,multinomialNeu)
	if(maxScore == multinomialPos){
		testData$multinomial[i] <- "positive"
	}else if (maxScore == multinomialNeg ){
		testData$multinomial[i] <- "negative"
	} else{
		testData$multinomial[i] <- "neutral"
	}
	
	maxScore <- max(bernuliPos,bernuliNeg,bernuliNeu)
	if(maxScore == bernuliPos){
		testData$bernuli[i] <- "positive"
	}else if (maxScore == bernuliNeg ){
		testData$bernuli[i] <- "negative"
	} else{
		testData$bernuli [i]<- "neutral"
	}


}

	
	accuracyMP <- (nrow(testData[testData$sentiment=='positive'& testData$multinomial == "positive",])/1000)
	accuracyMNG <-(nrow(testData[testData$sentiment=='negative'& testData$multinomial == "negative",])/1000)
	accuracyMNE <-(nrow(testData[testData$sentiment=='neutral'& testData$multinomial == "neutral",])/1000)
	
	accuracyBP <- (nrow(testData[testData$sentiment=='positive'& testData$bernuli == "positive",])/1000)
	accuracyBNG <-(nrow(testData[testData$sentiment=='negative'& testData$bernuli == "negative",])/1000)
	accuracyBNE <-(nrow(testData[testData$sentiment=='neutral'& testData$bernuli == "neutral",])/1000)
	
	results <- data.frame(
		Multi_Pos = accuracyMP,
		Multi_Neg = accuracyMNG,
		Multi_Neu = accuracyMNE,
		Bern_Pos = accuracyBP,
		Bern_Neg = accuracyBNG,
		Bern_Neu = accuracyBNE
	)
	print(results)





