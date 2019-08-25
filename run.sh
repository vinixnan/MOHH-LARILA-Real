#!/bin/bash


function runIt {
	qtdExp=30
	seed=-1
	kind=$1
	fixedGen=$2
	problemClass=$3
	qtdAlgs=$4
	
	
	qtdExp=30
	number=0
	
	while [ $number -le $qtdExp ]
	do
			echo "java -Xms1024m -Xmx1024m -cp target/MOHH-LARILA-1.0-SNAPSHOT.jar:target/lib/* HF_Main.RealProblemsLearningAutomataMain 0 $fixedGen $seed $kind $problemClass $qtdAlgs $number > $output 2> $erroutput" >> "runMain.txt"
			
			#echo "java -Xms1024m -Xmx1024m -cp 'target/MOHH-LARILA-1.0-SNAPSHOT.jar;target/lib/*' HF_Main.RealProblemsLearningAutomataMain 0 $fixedGen $seed $kind $problemClass $qtdAlgs $number > $output 2> $erroutput" >> "runMain.txt"
			let number=$number+1;
	done
	
	
	
}

rm -f "runMain.txt"
problemClasses="VC Real"
qtdAlgs=5
problemClasses="Real"

sizes="10"
sizes="5"
#sizes="2"

#kinds="1"
#for problemClass in $problemClasses
#do
#	for size in $sizes 
#	do
#		for kind in $kinds 
#		do
#			runIt $kind $size $problemClass $qtdAlgs
#		done	
#	done
#done

#kinds="2"
#sizes="25"
#problemClasses="VC"
#problemClasses="Real"

sizes="1"
kinds="1"
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



cat "runMain.txt" | xargs -I CMD -P 4  bash -c CMD &
wait
