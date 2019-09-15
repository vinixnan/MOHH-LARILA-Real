#!/bin/bash
java -Xms5024m -Xmx5024m -cp target/MOHH-LARILA-1.0-SNAPSHOT.jar:target/lib/* HF_Main.RealStudy > ellog.txt
wait
