#!/bin/bash -e
. linuxFileList.inc

#XXX hack
#scalac Stats.scala

java -jar sbt-launch-0.7.4.jar "project LinuxAnalysis" "pcpp-stats -f pcs/x86.flist pcppStats.csv"

#filesToProcess|while read i; do
#  ./parsingStats.sh $srcPath/$i.pi.dbgT linuxParse.csv
#done
