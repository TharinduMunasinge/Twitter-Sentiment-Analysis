load("neural.RData")
testdata <- as.data.frame((1:10)^2)
results <- compute(net.sqrt, testdata)
simple<-function(){
  return (100)
}

