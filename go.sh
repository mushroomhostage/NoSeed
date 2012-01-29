#!/bin/sh -x
CLASSPATH=../craftbukkit-1.1-R2.jar javac *.java -Xlint:deprecation -Xlint:unchecked
rm -rf me
mkdir -p me/exphc/Lost
mv *.class me/exphc/Lost/
jar cf Lost.jar me/ *.yml
cp Lost.jar ../plugins/
