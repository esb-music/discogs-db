class Musician < ApplicationRecord
  has_many :performings
  has_many :tracks, through: :performings

  def self.execute_sql(*sql_array)
    connection.execute(send(:sanitize_sql_array, sql_array))
  end

  def self.write_json(key)
    query = <<-SQL
select json_object('mid', id, 'name', name, 'info', info,
          'img', img, 'alt', alt, 'wiki', wiki, 'bdate', bdate, 'ddate', ddate) as musician, id as mid
          from musicians join
          (select mid, count(distinct aid) as albumcnt from musicians_on_album
			where key = ?
			group by mid) m
          on (musicians.id = m.mid) order by albumcnt desc, mid;
    SQL
    musicians = Musician.execute_sql(query, key).map { |e| JSON.parse(e.values[0]) }

    Dir.mkdir( "discogs-app-data/#{key}") unless Dir.exists?("discogs-app-data/#{key}")
    File.open("discogs-app-data/#{key}/musicians.json", "w") do |f|
      f.write(musicians.to_json)
    end
  end

end

