postscript("HV.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
filemyNSGAII<-paste(resultDirectory, "myNSGAII", sep="/")
filemyNSGAII<-paste(filemyNSGAII, problem, sep="/")
filemyNSGAII<-paste(filemyNSGAII, indicator, sep="/")
myNSGAII<-scan(filemyNSGAII)

filemySPEA2<-paste(resultDirectory, "mySPEA2", sep="/")
filemySPEA2<-paste(filemySPEA2, problem, sep="/")
filemySPEA2<-paste(filemySPEA2, indicator, sep="/")
mySPEA2<-scan(filemySPEA2)

filemyIBEA<-paste(resultDirectory, "myIBEA", sep="/")
filemyIBEA<-paste(filemyIBEA, problem, sep="/")
filemyIBEA<-paste(filemyIBEA, indicator, sep="/")
myIBEA<-scan(filemyIBEA)

filemyGDE3<-paste(resultDirectory, "myGDE3", sep="/")
filemyGDE3<-paste(filemyGDE3, problem, sep="/")
filemyGDE3<-paste(filemyGDE3, indicator, sep="/")
myGDE3<-scan(filemyGDE3)

filemIBEA<-paste(resultDirectory, "mIBEA", sep="/")
filemIBEA<-paste(filemIBEA, problem, sep="/")
filemIBEA<-paste(filemIBEA, indicator, sep="/")
mIBEA<-scan(filemIBEA)

algs<-c("myNSGAII","mySPEA2","myIBEA","myGDE3","mIBEA")
boxplot(myNSGAII,mySPEA2,myIBEA,myGDE3,mIBEA,names=algs, notch = TRUE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(3,3))
indicator<-"HV"
qIndicator(indicator, "WFG1")
qIndicator(indicator, "WFG2")
qIndicator(indicator, "WFG3")
qIndicator(indicator, "WFG4")
qIndicator(indicator, "WFG5")
qIndicator(indicator, "WFG6")
qIndicator(indicator, "WFG7")
qIndicator(indicator, "WFG8")
qIndicator(indicator, "WFG9")
