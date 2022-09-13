#!/bin/zsh
# 
# Making the json files for discogs-app from discogs.sqlite
# © 2022 by Burkhardt Renz, THM, no rights reserved
#

keys=('cream' 'jimi')

# function pretty printing json
function pretty-print {
	mv $1 tmp;
	json_reformat < tmp > $1
	rm tmp;
}

# discogs.json
mkdir json
java -jar discogs-db.jar discogs
pretty-print discogs.json;
mv discogs.json json 

# jsons for the key
for key ($keys) {
	mkdir json/$key;
	java -jar discogs-db.jar albums $key;
	pretty-print $key-albums.json;
	mv $key-albums.json json/$key/albums.json;
	java -jar discogs-db.jar musicians $key;
	pretty-print $key-musicians.json;
	mv $key-musicians.json json/$key/musicians.json;
	java -jar discogs-db.jar tracks $key;
	pretty-print $key-tracks.json;
	mv $key-tracks.json json/$key/tracks.json;
}	


