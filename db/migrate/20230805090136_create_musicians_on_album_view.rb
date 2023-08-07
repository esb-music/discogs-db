class CreateMusiciansOnAlbumView < ActiveRecord::Migration[7.0]
  def change
    execute <<-SQL
        create view musicians_on_album as
            select key, albums.id as aid, musicians.id as mid, musicians.name as name from discogs join albums on (discogs.id = albums.discog_id)
	              join tracks on (albums.id = tracks.album_id)
	              join performings on (tracks.id = performings.track_id)
	              join musicians on (performings.musician_id = musicians.id)
	              group by key, albums.id, musicians.id, musicians.name;
    SQL
  end
end
