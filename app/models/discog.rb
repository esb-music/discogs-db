class Discog < ApplicationRecord
  has_many :albums

  # Write discogs.json for discogs-app
  def self.write_json
    discogs = Discog.select(:key, :name, :img, :alt, :wiki, :active).select { |d| d.active == 1 }.to_json
    # Values in jresult have a superfluous key id - we delete it
    result = JSON.parse(discogs)
    result.each do |d|
      d.delete("id")
    end
    File.open("discogs-app-data/discogs.json", "w") do |f|
      f.write(result.to_json)
    end
  end
end
