#!/bin/bash


function runIt {
	qtdExp=40
	seed=-1
	kind=$1
	fixedGen=$2
	problemClass=$3
	qtdAlgs=$4
	output="out_"$kind"_"$fixedGen"_"$problemClass"_"$qtdAlgs"_ext2"
	erroutput="err_"$kind"_"$fixedGen"_"$problemClass"_"$qtdAlgs"_ext2"
	
	number=1
	while [ $number -le $qtdExp ]
	do
			echo "java -Xms1024m -Xmx1024m -cp target/MOHH-LARILA-1.0-SNAPSHOT.jar:target/lib/* HF_Main.RealProblemsLearningAutomataMain $qtdRun $fixedGen $seed $kind $problemClass $qtdAlgs $number> $output 2> $erroutput" >> "runMain.txt"
			#echo "java -Xms1024m -Xmx1024m -cp 'target/MOHH-LARILA-1.0-SNAPSHOT.jar;target/lib/*' HF_Main.RealProblemsLearningAutomataMain $qtdRun $fixedGen $seed $kind $problemClass $qtdAlgs $number > $output 2> $erroutput" >> "runMain.txt"
			let number=$number+1;
	done
	
	
	
}

rm -f "runMain.txt"
kinds="0 1"
problemClasses="VC Real"
qtdAlgs=5
sizes="10"

for problemClass in $problemClasses
do
	for size in $sizes 
	do
		for kind in $kinds 
		do
			runIt $kind $size $problemClass $qtdAlgs
		done	
	done
done

cat "runMain.txt" | xargs -I CMD -P 5  bash -c CMD &
wait
