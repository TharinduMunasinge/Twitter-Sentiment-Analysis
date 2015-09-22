library("wordnet")

#Get list of antonyms from wordnet
antonyms<- function(x){
    filter <- getTermFilter("ExactMatchFilter", x, TRUE)
    terms <- getIndexTerms("ADJECTIVE", 5, filter)
    if (is.null(terms)){
		return(NULL)
    }
    synsets <- getSynsets(terms[[1]])
    related <- tryCatch(
        getRelatedSynsets(synsets[[1]], "!"),
        error = function(condition) {
            message("No direct antonym found")
            if (condition$message == "RcallMethod: invalid object parameter")
                message("No direct antonym found")
            else
                stop(condition)
            return(NULL)
        }
    )
    if (is.null(related))
        return(NULL)
    return(sapply(related, getWord))
}

#determine strength of verb,adverb,adjective
determineOrientation <- function(token, seedList, pos){
	if (token %in% seedList[,1]){
		return (seedList$strength[match(token,seedList[,1])])
	}

	synonymList <- synonyms(token,pos)

	if (!is.character(synonymList)){
		for (i in 1:nrow(synonymList)){
			if (synonymList[i] %in% seedList[,1]){
				strength <- seedList$strength[match(synonymList[i],seedList[,1])]
				#appendList <- data.frame(c(token,strength))
				#seedList <- rbind(seedList, appendList)
				return (strength)
			}
		}
	}

	antonymsList <- NULL	
	antonymsList <- antonyms(token)
	if (!is.null(antonymsList)){
		for (i in 1:length(antonymsList)){
			if (antonymsList[i] %in% seedList[,1]){
				strength <- -seedList$strength[match(antonymsList[i],seedList[,1])]
				#appendList <- data.frame(c(token,strength))
				#seedList <- rbind(seedList, appendList)
				return (strength)
			}
		}
	}	
	return (0)
}

determineAdverbStrength <- function(token, seedList){
	return (determineOrientation(token,seedList,"ADVERB"))
}

determineVerbStrength <- function (token, seedList){
	return (determineOrientation(token,seedList,"VERB"))
}

determineAdjectiveStrength <- function(token, seedList){
	return (determineOrientation(token,seedList,"ADJECTIVE"))
}


extractAdjectiveGroup <- function(taggedSentence, adjectiveScoreList, adverbScoreList){
	adjectiveGroupList <- data.frame()
	
	for (i in 1:nrow(taggedSentence)){		
		if(taggedSentence$Tag[i] == "adjective"){
			if (taggedSentence$Tag[i-1] == "adverb"){
				score <- determineAdjectiveStrength(taggedSentence$Token[i],adjectiveScoreList)*determineAdverbStrength(taggedSentence$Token[i-1],adverbScoreList)				
				adjectiveAdverb <- data.frame(class=sprintf("%s %s",taggedSentence$Token[i-1],taggedSentence$Token[i]), score=score)
				adjectiveGroupList <- rbind(adjectiveGroupList, adjectiveAdverb)
			}else{
				score <- determineAdjectiveStrength(taggedSentence$Token[i],adjectiveScoreList)*0.5	
				adjectiveAdverb <- data.frame(class=sprintf("%s",taggedSentence$Token[i]), score=score)
				adjectiveGroupList <- rbind(adjectiveGroupList, adjectiveAdverb)
			}
		}
	}
	
	return(adjectiveGroupList)
}

extractVerbGroup <- function(taggedSentence, verbScoreList, adverbScoreList){
	verbGroupList <- data.frame()
	for (i in 1:nrow(taggedSentence)){
		 if(taggedSentence$Tag[i] == "verb"){
			if (taggedSentence$Tag[i-1] == "adverb"){
				score <-determineVerbStrength(taggedSentence$Token[i],verbScoreList)*determineAdverbStrength(taggedSentence$Token[i-1],adverbScoreList)				
				adverbVerb <- data.frame(class=sprintf("%s %s",taggedSentence$Token[i-1],taggedSentence$Token[i]), score=score)
				verbGroupList <- rbind(verbGroupList, adverbVerb)
			}else{
				score <- determineVerbStrength(taggedSentence$Token[i],verbScoreList)*0.5	
				adverbVerb <- data.frame(class=sprintf("%s",taggedSentence$Token[i]), score=score)
				verbGroupList <- rbind(verbGroupList, adverbVerb)
			}
				
		}

	}
	
	return (verbGroupList)
}

#FULL CAP words as a percentage
capPercentage <- function(sentence){
  listOfWords <- strsplit(sentence, " ")[[1]]
  count <- length(grep("[[:lower:]]", listOfWords, invert = TRUE))
  percentage <- count / length(listOfWords)
  return(percentage)
}

#Tag part of speech
posTagger <- function(corpus){
	x<-treetag(corpus, treetagger='manual', lang='en', TT.options=list(path='/Development/ProgramFiles/tree-tagger', preset='en'))
	return (data.frame(Token=x@TT.res$token, Tag=x@TT.res$wclass))
}

#Emoticon Strength
extractEmoticonGroup <- function(tweet, emoticonList){
	tokenList <- strsplit(tweet," ")[[1]]
	emoticonGroup <- data.frame()
	for (i in 1:length(tokenList)){
		if (tokenList[i] %in% emoticonList$Emoticon){
			if (nrow(emoticonGroup) != 0){
				if(tokenList[i] %in% emoticonGroup$emoticon){
					match(tokenList[i],emoticonGroup$emoticon)
					emoticonGroup$count[match(tokenList[i],emoticonGroup$emoticon)] <- emoticonGroup$count[match(tokenList[i],emoticonGroup$emoticon)] + 1
				}else{
					emoticon <- data.frame(emoticon=tokenList[i], strength=emoticonList$Strength[match(tokenList[i],emoticonList$Emoticon)], count =1)
					emoticonGroup <- rbind(emoticonGroup,emoticon)
				}
			}
			else{
				emoticon <- data.frame(emoticon=tokenList[i], strength=emoticonList$Strength[match(tokenList[i],emoticonList$Emoticon)], count =1)
				emoticonGroup <- rbind(emoticonGroup,emoticon)
			}
		}
	}
	return (emoticonGroup)
}


#Percentage of Caps in the text
#for(i in 1:nrow(tweets)){
#	tweets$capPercentage[i] <- capPercentage(tweets$text[i])
#}
