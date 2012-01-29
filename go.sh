#!/bin/sh -x
#CLASSPATH=../craftbukkit-1.1-R2.jar javac *.java -Xlint:deprecation -Xlint:unchecked
javac LostLoader.java
rm -rf me
mkdir -p me/exphc/Lost
mv *.class me/exphc/Lost/
jar cvMf Lost.jar me/ *.yml META-INF
#cp Lost.jar ../plugins/
