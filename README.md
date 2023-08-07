# _discogs-db_: Database and Tools for Discographies

_discogs-db_ contains the schema of an SQLite database
for storing informations about discographies, as well as
the SQLite database itself used for the website
[esb-music.github.io](https://esb-music.github.io).

The project includes tools for generating and validating
JSON files used by [discogs-app](https://github.com/esb-music/discogs-app)
to provide the data for [esb-music.github.io](https://esb-music.github.io).

## Structure of the database

The schema of the SQLite database is 
visualized in the following data structure diagram:
![data struture diagram of the database](discogs-schema.svg)

## How to use _discogs-db_

_discogs_db_ is a Ruby on Rails app. The simplest way to start the app:
Load the project into RubyMine, start the built-in webserver via the command
'rails s' and start the browser with the URL shown in the terminal.

1. Populate the database with information of the albums of a
   musician or a band. The table 'discogs' contains the record
   with the data on this discography. The table 'albums' lists the
   albums, the table 'tracks' the tracks on the albums. The table
   'performings' connects the musicians with the tracks they play on.
2. The JSON files for _discogs-app_ can be generated in the exports page 
   of the app.

