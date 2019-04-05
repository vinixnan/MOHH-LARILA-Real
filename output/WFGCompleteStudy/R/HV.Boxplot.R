postscript("HV.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileNSGAII<-paste(resultDirectory, "NSGAII", sep="/")
fileNSGAII<-paste(fileNSGAII, problem, sep="/")
fileNSGAII<-paste(fileNSGAII, indicator, sep="/")
NSGAII<-scan(fileNSGAII)

fileSPEA2<-paste(resultDirectory, "SPEA2", sep="/")
fileSPEA2<-paste(fileSPEA2, problem, sep="/")
fileSPEA2<-paste(fileSPEA2, indicator, sep="/")
SPEA2<-scan(fileSPEA2)

fileIBEA<-paste(resultDirectory, "IBEA", sep="/")
fileIBEA<-paste(fileIBEA, problem, sep="/")
fileIBEA<-paste(fileIBEA, indicator, sep="/")
IBEA<-scan(fileIBEA)

fileGDE3<-paste(resultDirectory, "GDE3", sep="/")
fileGDE3<-paste(fileGDE3, problem, sep="/")
fileGDE3<-paste(fileGDE3, indicator, sep="/")
GDE3<-scan(fileGDE3)

algs<-c("NSGAII","SPEA2","IBEA","GDE3")
boxplot(NSGAII,SPEA2,IBEA,GDE3,names=algs, notch = TRUE)
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
