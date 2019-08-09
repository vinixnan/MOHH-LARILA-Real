#!/bin/bash
java -Xms4024m -Xmx4024m -cp target/MOHH-LARILA-1.0-SNAPSHOT.jar:target/lib/* HF_Main.RealStudy > ellog.txt
wait
