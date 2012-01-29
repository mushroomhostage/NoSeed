#!/bin/sh -x
#CLASSPATH=../craftbukkit-1.1-R2.jar javac GetLost.java -Xlint:deprecation -Xlint:unchecked
javac GetLostLoader.java
rm -rf me
mkdir -p me/exphc/GetLost
mv *.class me/exphc/GetLost/
cp ../dev/CraftBukkit/target/classes/net/minecraft/server/Packet1Login.class .
jar cvMf GetLost.jar me/ META-INF Packet1Login.class
#cp GetLost.jar ../plugins/
