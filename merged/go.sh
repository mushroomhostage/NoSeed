#!/bin/sh -x
CLASSPATH=../../craftbukkit-1.1-R2.jar javac *.java -Xlint:deprecation
rm -rf me 
mkdir -p me/exphc/NoSeed
mv *.class me/exphc/NoSeed
cp ../Packet1Login.class Packet1Login.class.noseed
jar cvMf NoSeed.jar me/ *.yml *.java META-INF Packet1Login.class.noseed
cp NoSeed.jar ../../plugins/
