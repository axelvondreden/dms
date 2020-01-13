#!/bin/bash
java -jar "-Dvaadin.productionMode=true" ./dms.jar
code=$?
echo $code
if [ $code -eq 1337 ] || [ $code -eq 57 ]
then
  echo "Deleting old files..."
  rm lipsum.txt options.default.properties dms.jar dms.zip

  echo "Copying new files..."
  rsync -avr --progress "update/" "./"
  rm -rf update

  echo "Starting updated dms..."
  java -jar "-Dvaadin.productionMode=true" ./dms.jar
fi
