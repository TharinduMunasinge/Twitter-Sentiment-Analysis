#Pre process Tweets
library(koRpus)

#Remove URL
removeURL <- function (corpus){
	return (gsub("(f|ht)tp(s?)://(.*)[.][a-z]+", "", corpus))
}


#Remove User Tag
removeUserTag <- function (corpus){
	return (gsub("@.([A-Z]|[a-z]|[0-9]|_)+", "", corpus))
}

#Remove Special Characters such as RT
removeSpecialTag <-function(corpus){
	return (gsub("RT +", "", corpus))
}
