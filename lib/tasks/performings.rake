namespace :performings do
  desc "Propagates performings of musician to all tracks of an album"
  task propagate: :environment do
    puts "Enter album id:"
    album_id = STDIN.gets.chomp
    puts "Enter musician id:"
    musician_id = STDIN.gets.chomp
    puts "Enter instrument(s), the musicians is playing in that album:"
    instruments = STDIN.gets.chomp
    album = Album.find(album_id)
    musician = Musician.find(musician_id)
    puts "Propagate #{musician.name} playing #{instruments} on #{album.title}? [y/n]"
    go = STDIN.gets.chomp
    if go == "y"
      for track in album.tracks do
        performing = Performing.new do |p|
          p.instruments = instruments
          p.musician = musician
          p.track = track
        end
        performing.save!
      end
    end
  end
end
