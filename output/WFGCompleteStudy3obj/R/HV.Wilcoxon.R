write("", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex",append=FALSE)
resultDirectory<-"output/WFGCompleteStudy3obj/data"
latexHeader <- function() {
  write("\\documentclass{article}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\title{StandardStudy}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\usepackage{amssymb}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\author{A.J.Nebro}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{document}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\maketitle", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\section{Tables}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {
  write("\\begin{table}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\caption{", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\label{Table:", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(problem, "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(".HV.}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)

  write("\\centering", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{scriptsize}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\begin{tabular}{", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(tabularString, "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write(latexTableFirstLine, "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\hline ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}

printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { 
  file1<-paste(resultDirectory, algorithm1, sep="/")
  file1<-paste(file1, problem, sep="/")
  file1<-paste(file1, indicator, sep="/")
  data1<-scan(file1)
  file2<-paste(resultDirectory, algorithm2, sep="/")
  file2<-paste(file2, problem, sep="/")
  file2<-paste(file2, indicator, sep="/")
  data2<-scan(file2)
  if (i == j) {
    write("--", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  }
  else if (i < j) {
    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {
      if (median(data1) >= median(data2)) {
        write("$\\blacktriangle$", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}
      else {
        write("$\\triangledown$", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}
}
    else {
      write("$-$", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}
  }
  else {
    write(" ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  }
}

latexTableTail <- function() { 
  write("\\hline", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{tabular}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{scriptsize}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
  write("\\end{table}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}

latexTail <- function() { 
  write("\\end{document}", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
}

### START OF SCRIPT 
# Constants
problemList <-c("WFG1", "WFG2", "WFG3", "WFG4", "WFG5", "WFG6", "WFG7", "WFG8", "WFG9") 
algorithmList <-c("myNSGAII", "mySPEA2", "myIBEA", "myGDE3", "mIBEA") 
tabularString <-c("lcccc") 
latexTableFirstLine <-c("\\hline  & mySPEA2 & myIBEA & myGDE3 & mIBEA\\\\ ") 
indicator<-"HV"

 # Step 1.  Writes the latex header
latexHeader()
tabularString <-c("| l | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm }p{0.15cm } | ") 

latexTableFirstLine <-c("\\hline \\multicolumn{1}{|c|}{} & \\multicolumn{9}{c|}{mySPEA2} & \\multicolumn{9}{c|}{myIBEA} & \\multicolumn{9}{c|}{myGDE3} & \\multicolumn{9}{c|}{mIBEA} \\\\") 

# Step 3. Problem loop 
latexTableHeader("WFG1 WFG2 WFG3 WFG4 WFG5 WFG6 WFG7 WFG8 WFG9 ", tabularString, latexTableFirstLine)

indx = 0
for (i in algorithmList) {
  if (i != "mIBEA") {
    write(i , "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
    write(" & ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)

    jndx = 0
    for (j in algorithmList) {
      for (problem in problemList) {
        if (jndx != 0) {
          if (i != j) {
            printTableLine(indicator, i, j, indx, jndx, problem)
          }
          else {
            write("  ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
          } 
          if (problem == "WFG9") {
            if (j == "mIBEA") {
              write(" \\\\ ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
            } 
            else {
              write(" & ", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
            }
          }
     else {
    write("&", "output/WFGCompleteStudy3obj/R/HV.Wilcoxon.tex", append=TRUE)
     }
        }
      }
      jndx = jndx + 1
}
    indx = indx + 1
  }
} # for algorithm

  latexTableTail()

#Step 3. Writes the end of latex file 
latexTail()

