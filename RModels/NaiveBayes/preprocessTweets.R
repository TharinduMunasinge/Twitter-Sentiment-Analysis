#Pre process Tweets
library(koRpus)

#Remove URL
removeURL <- function (corpus){
	for(i in 1:nrow(corpus)){
		corpus$text[i] <- gsub("(f|ht)tp(s?)://\\S+", "", corpus$text[i])
	}
	return (corpus)
}


#Remove User Tag
removeUserTag <- function (corpus){
	for(i in 1:nrow(corpus)){
		corpus$text[i] <- gsub("@.([A-Z]|[a-z]|[0-9]|_)+", "", corpus$text[i])
	}
	return (corpus)	
}

#Remove Special Characters such as RT
removeSpecialTag <-function(corpus){
	for(i in 1:nrow(corpus)){
		corpus$text[i] <- gsub("RT +", "", corpus$text[i])
	}
	return (corpus)
}
