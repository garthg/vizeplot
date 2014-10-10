#!/bin/bash
cd `dirname $0`
here="`pwd`"
libfiles="$here/libs/opencsv-3.0.jar"
cd src
cmd="find . -name '*.java' | xargs javac -cp $libfiles"
echo "$cmd"
eval "$cmd"
