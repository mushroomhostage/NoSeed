#!/bin/sh -x
CLASSPATH=../../craftbukkit-1.1-R3.jar javac NoSeed.java -Xlint:deprecation
rm -rf me 
mkdir -p me/exphc/NoSeed
mv *.class me/exphc/NoSeed
cp ../Packet1Login.class Packet1Login.class.noseed
cp ../Packet1Login.java Packet1Login.java.noseed
jar cvMf NoSeed.jar me/ *.yml *.java META-INF Packet1Login.*.noseed README ChangeLog
cp NoSeed.jar ../../plugins/
