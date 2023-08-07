class Album < ApplicationRecord
  belongs_to :discog
  has_many   :tracks

  def self.execute_sql(*sql_array)
    connection.execute(send(:sanitize_sql_array, sql_array))
  end

  def self.write_json(key)
    query = <<-SQL
select json_object(
          'key', key, 'aid', aid, 'title', title, 'subtitle', subtitle, 'img', img,
          'alt', alt, 'wiki', wiki, 'label', label, 'released', released,
          'recorded', recorded, 'producer', producer, 'venue', venue, 'length', length,
          'musicians', json_group_array(json(json_object('mid', mid, 'name', name)))) as album
          from albums left join musicians_on_album on (albums.id = aid) where key = ?
          group by aid;
    SQL
    albums = Album.execute_sql(query, key).map { |e| JSON.parse(e.values[0]) }

    Dir.mkdir( "discogs-app-data/#{key}") unless Dir.exists?("discogs-app-data/#{key}")
    File.open("discogs-app-data/#{key}/albums.json", "w") do |f|
      f.write(albums.to_json)
    end
  end
end
