#!/bin/sh

mvn clean install -DskipTests

cd '/Users/aramos/Documents/TEMP/Dev Shims/common'
rm -rf *
cp '/Users/aramos/Documents/Hitachi/REPOS/DEV/pentaho-hadoop-shims/shims/hdp26/common/target/pentaho-hadoop-shims-hdp26-common-9.0.0.0-SNAPSHOT.jar' .
jar xf *.jar
cd '/Users/aramos/Documents/Hitachi/REPOS/DEV/pentaho-hadoop-shims/shims/hdp26/common'