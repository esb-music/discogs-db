# _discogs-db_: Database and Tools for Discographies

_discogs-db_ contains the schema of an SQLite database 
for storing informations about discographies, as well as 
the SQLite database itself used for the website
[esb-music.github.io](https://esb-music.github.io).

The project includes tools for generating and validating
JSON files used by [discogs-app](https://github.com/esb-music/discogs-app)
to provide the data for [esb-music.github.io](https://esb-music.github.io). 

## Structure of the database

The schema of the SQLite database is defined in 
<a href="discogs.ddl" target="_blank">discogs.ddl</a> and visualized 
in the following data structure diagram:

![data struture diagram of the database](discogs-schema.svg)

## How to use _discogs-db_

1. Populate the database with the informations of the albums of a 
   musician or a band. The table 'discogs' contains the record
   with the data on this discography. The table 'albums' lists the 
   albums, the table 'tracks' the tracks on the albums. The table
   'performing' connects the musicians with the tracks they play on.

   There are a lot of records in the table 'performing'. discogs.db
   facilitates entering these data with the command `propagate`. 
   This command allows you to assign a musician and his instrument(s)
   to all tracks on an album.
2. The JSON files for _discogs-app_ are generated with _discogs-db_.
   The keys of the discographies from table 'discogs' are written in the
   shell script `generate.sh`. It then generates all required JSON files.
3. _discogs-db_ provides a command to validate the generated JSON files,
   see `validate.sh` how to do this.

## The commands provided by _discogs-db_

- `java discogs.jar discogs` generates `discogs.json`.
- `java discogs.jar albums <key>` generates `<key>-albums.json` for the given key.
- `java discogs.jar musicians <key>`generates `<key>-musicians.json`.
- `java discogs.jar tracks <key>` generates `<key>-tracks.json`.
- `java discogs.jar propagate <key>` propagates musician and instrument to all tracks of an album.
- `java discogs.jar validate <json-file> <json-schema>` validates `<json-file>` with respect to `<json-schema>`.
   
   
