#!/bin/zsh
# 
# Validate the json files for discogs-app
# © 2022 by Burkhardt Renz, THM, no rights reserved
#

echo "validate discogs.json"
java -jar discogs-db.jar validate json/discogs.json schemes/discogs.schema.json;

keys=('cream' 'jimi')

for key ($keys) {
  echo "validate $key"
  java -jar discogs-db.jar validate json/$key/albums.json schemes/albums.schema.json;
  java -jar discogs-db.jar validate json/$key/musicians.json schemes/musicians.schema.json;
  java -jar discogs-db.jar validate json/$key/tracks.json schemes/tracks.schema.json;
}	


