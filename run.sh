#!/bin/bash
cd `dirname $0`
here="`pwd`"
libfiles="$here/libs/opencsv-3.0.jar"
cd src
cmd="java -cp .:$libfiles main.Main"
echo "$cmd"
eval "$cmd"
