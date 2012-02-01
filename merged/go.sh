#!/bin/sh -x
CLASSPATH=../../craftbukkit-1.1-R2.jar javac *.java -Xlint:deprecation
rm -rf me 
mkdir -p me/exphc/NoSeed
mv *.class me/exphc/NoSeed
jar cf NoSeed.jar me/ *.yml *.java
cp NoSeed.jar ../../plugins/
