#!/bin/bash

pushd src
javac App.java
jar cfe SecureChat.jar App *.class ../author_icon.jpeg
rm *.class
popd

cp src/SecureChat.jar ./
chmod u+x SecureChat.jar

echo "Usage: java -jar SecureChat.jar"
