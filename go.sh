#!/bin/sh -x
#CLASSPATH=../craftbukkit-1.1-R2.jar javac NoSeed.java -Xlint:deprecation -Xlint:unchecked
CLASSPATH=../craftbukkit-1.1-R2.jar javac NoSeedLoader.java
rm -rf me
mkdir -p me/exphc/NoSeed
mv *.class me/exphc/NoSeed/
#cp ../dev/CraftBukkit/target/classes/net/minecraft/server/Packet1Login.class .
jar cvMf NoSeed.jar me/ META-INF *.java
#cp NoSeed.jar ../plugins/
