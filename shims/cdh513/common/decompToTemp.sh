#!/bin/sh

mvn clean install -DskipTests

cd '/Users/aramos/Documents/TEMP/TRASH/CommonConHive'
rm -rf *
cp '/Users/aramos/Documents/Hitachi/REPOS/DEV/pentaho-hadoop-shims/shims/cdh513/common/target/pentaho-hadoop-shims-cdh513-common-9.0.0.0-SNAPSHOT.jar' .
jar xf *.jar
cd '/Users/aramos/Documents/Hitachi/REPOS/DEV/pentaho-hadoop-shims/shims/cdh513/common'