/*
 Schema of the discogs database
 © 2022, Burkhardt Renz, THM, no rights reserved
 */
 
/*
 Table discogs
 */
create table discogs(
  key text primary key,         -- short string identifying the discography
  name text not null,           -- display name
  img text not null,            -- url of image for the band or musician
  alt text not null,            -- alternate text for the image
  wiki text not null            -- url of wiki article of band or musician
);

/*
 Table album
 Album in a discog collection
 */
create table albums (
  key text references discogs,  -- reference to discog
  aid int,                      -- id of album within discog
  title text not null,          -- title of album
  subtitle text not null,       -- subtitle of album
  img text not null,            -- url of cover
  alt text not null,            -- alternate text for the image
  wiki text not null,           -- url of wiki article of cover or musician
  label text,                   -- label of release
  released text not null,       -- date of release
  recorded text,                -- date and studio of recording
  producer text,                -- producer
  venue text,                   -- venue in case of live recording
  length decimal(6,2),          -- length
  primary key (key, aid),       -- aid is unique per key
  unique (key, title)           -- title is unique per key
);

/*
  Table tracks
  track no tno on album aid
 */
 
create table tracks (
  key text,                     -- reference to discog
  aid int,                      -- id of album within discog
  tno int,                      -- track number
  title text not null,          -- title of track
  length text not null,         -- length of track
  primary key (key, aid, tno),
  foreign key (key, aid) references albums(key, aid)
);

/*
 Table musicians
 Global table, the same musician can appear in different discogs
 */
create table musicians (
  mid int primary key,          -- unique id of musician, used for sorting on album card
  name text not null unique,    -- unique name of musician
  info text not null,           -- short characteristics of the musician 
  img text,                     -- url image of musician
  alt text,                     -- alternate text for the image
  wiki text,                    -- url of wiki article of musician
  bdate date,
  ddate date
);

/* 
 Table performing
 Musician mid performing a track playing instruments
 */
create table performing (
  mid int references musicians,  -- id of musician
  key text,                      -- key of discog
  aid int,                       -- id of album
  tno int,                       -- no of track on album
  instruments text not null,     -- instrument(s) played
  primary key (mid, key, aid, tno),
  foreign key (key, aid, tno) references tracks(key, aid, tno)
);

/* View musician_on_album
   Musicians performing on album
 */
create view musician_on_album as
select distinct key, aid, mid, name
    from performing natural join musicians;
