class Track < ApplicationRecord
  belongs_to :discog
  belongs_to :album
  has_many :performings
  has_many :musicians, through: :performings

  def self.execute_sql(*sql_array)
    connection.execute(send(:sanitize_sql_array, sql_array))
  end

  def self.write_json(key)
    query = <<-SQL
with p as (
	select key, album_id as aid, tno, json_group_array(json(json_object('name', musicians.name, 'instruments', instruments))) as performing
		from discogs join tracks on (discogs.id = tracks.discog_id)
			join performings on (performings.track_id = tracks.id)
			join musicians on (performings.musician_id = musicians.id)
			where key = ?
			group by album_id, tno),
t as (
	select p.key key, album_id aid, json_object('tno', p.tno, 'title', title, 'length', length,'performing', json(performing)) as track 
          from discogs join tracks on (discogs.id = tracks.discog_id)
          	join p on (p.key = discogs.key and p.aid = tracks.album_id and p.tno = tracks.tno) 
),
a as (
	select t.key key, albums.id aid, json_object('key', t.key, 'aid', t.aid, 'title', title, 'tracks', json_group_array(json(track))) as tracks
          from discogs join albums on (discogs.id = albums.discog_id)
          	join t on (t.key = discogs.key and t.aid = albums.id)
          	group by aid
)			
select tracks from a;
    SQL
    tracks = Track.execute_sql(query, key).map { |e| JSON.parse(e.values[0]) }

    Dir.mkdir( "discogs-app-data/#{key}") unless Dir.exists?("discogs-app-data/#{key}")
    File.open("discogs-app-data/#{key}/tracks.json", "w") do |f|
      f.write(tracks.to_json)
    end
  end

  def self.get_pairings(musician1, musician2)
    query = <<-SQL
select tracks.* from tracks join performings on performings.track_id = tracks.id 
    where performings.musician_id = ?
intersect
select tracks.* from tracks join performings on performings.track_id = tracks.id 
    where performings.musician_id = ?
order by tracks.album_id, tracks.tno
    SQL
    tracks = Track.execute_sql(query, musician1, musician2).map{ |t| Track.new(t) }
    return tracks
  end
end

