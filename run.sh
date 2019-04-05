#!/bin/bash


function runIt {
	qtdRun=40
	seed=-1
	kind=$1
	fixedGen=$2
	problemClass=$3
	qtdAlgs=$4
	output="out_"$kind"_"$fixedGen"_"$problemClass"_"$qtdAlgs
	erroutput="err_"$kind"_"$fixedGen"_"$problemClass"_"$qtdAlgs
	echo "java -Xms1024m -Xmx1024m -cp target/MOHH-LARILA-1.0-SNAPSHOT.jar:target/lib/* HF_Main.RealProblemsLearningAutomataMain $qtdRun $fixedGen $seed $kind $problemClass $qtdAlgs > $output 2> $erroutput" >> "runMain.txt"
}

rm -f "runMain.txt"
kinds="0 1"
sizes="50"
problemClass="WFG"
qtdAlgs=3


for size in $sizes 
do
	for kind in $kinds 
	do
		runIt $kind $size $problemClass $qtdAlgs
	done	
done

cat "runMain.txt" | xargs -I CMD -P 2  bash -c CMD &
wait
