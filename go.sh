#!/bin/sh -x
#CLASSPATH=../craftbukkit-1.1-R2.jar javac *.java -Xlint:deprecation -Xlint:unchecked
javac LostLoader.java
rm -rf me
mkdir -p me/exphc/Lost
mv *.class me/exphc/Lost/
cp ../dev/CraftBukkit/target/classes/net/minecraft/server/Packet1Login.class .
jar cvMf Lost.jar me/ *.yml META-INF Packet1Login.class
#cp Lost.jar ../plugins/
