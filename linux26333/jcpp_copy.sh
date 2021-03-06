#!/bin/bash 


#javaOpts='-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044'
javaOpts=''
javaOpts='$javaOpts -Xmx2G -Xms128m -Xss10m'

# For Java compiled stuff!
basePath=.

#mainClass="org.anarres.cpp.Main"
mainClass="de.fosd.typechef.Frontend"

# Brute argument parsing
# The right thing to do would be to be a gcc replacement, parse its flags and
# select the ones we care about.
if [ $# -lt 1 ]; then
  echo "Not enough arguments!" >&2
  exit 1
fi
inp=$1
shift

if [ -z "$inp" ]; then
  echo "inp not defined!" >&2
  exit 1
fi


outBase="$(dirname $inp)/$(basename $inp .c)"
outDbg="$outBase.dbg"
outErr="$outBase.err"
outTime="$outBase.time"
outAST="$outBase.tunit"
outStmtDegree="$outBase.stmt.degree"
outErrorStmtDegree="$outBase.stmt.error.degree"
outcfgDegree="$outBase.cfgdegree"
outasTime="$outBase.astimes"

copyBase=$(echo $outBase | sed s/local/scratch/g)
copyAllStmts="$copyBase.stmts.gz"
copyErrRaw="$copyBase.errRaw.gz"
copyDbg="$copyBase.dbg.gz"
copyErr="$copyBase.err.gz"
copyTime="$copyBase.time"
copyAST="$copyBase.tunit"
copyStmtDegree="$copyBase.stmt.degree.tar.gz"
copyErrorStmtDegree="$copyBase.stmt.error.degree.tar.gz"
copycfgDegree="$copyBase.cfgdegree.tar.gz"
rawcfgDegree="$copyBase.cfgdegree_raw.gz"
copyasTime="$copyBase.astimes"


# Beware: the embedded for loop requotes the passed argument. That's dark magic,
# don't ever try to touch it. It simplifies your life as a user of this program
# though!
echo "==Partially preprocessing $inp"
echo $partialPreprocFlags

bash -c "time ../../TypeChef/typechef.sh \
  $(for arg in $partialPreprocFlags "$@"; do echo -n "\"$arg\" "; done) \
  '$inp' 2> '$outErr' |tee '$outDbg'" \
  2> "$outTime" || true

cat "$outErr" 1>&2

#greps
#allErrors


#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*is freed multiple times" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"  > $copyBase.df
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*is freed although not dynamically allocted!" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"  > $copyBase.xf
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*is used uninitialized!" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"  > $copyBase.ui
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*Case statement is not terminated by a break!" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"  > $copyBase.cs
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*switch statement has dangling code" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"   >  $copyBase.dc
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*is not properly checked for" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat"  > $copyBase.std
#grep  --no-filename -o  "^[0-9][0-9]*.*Feature.*is a dead store" $outbase.allErrF | grep -o "^[0-9][0-9]*.*Feat" > $copyBase.ds

#copy
#mv $outDbg.gz $copyDbg
#cp $outErr $copyErr
#cp $outTime $copyTime
#cp $outsaTime $copysaTime 

#compression
gzip -c ${outBase}.err > ${copyBase}_vaa.err.gz
gzip -c ${outBase}.dbg > ${copyBase}_vaa.dbg.gz
#awk '{ print $1 }' $copycfgDegree | sort | gzip > $rawcfgDegree 
#tar cfvz $copycfgDegree $outcfgDegree
#rm $outcfgDegree 
#rm $outStmtDegree
#rm $outErrorStmtDegree
#rm $outcfgDegree
#rm $outDbg
#rm $outErr
