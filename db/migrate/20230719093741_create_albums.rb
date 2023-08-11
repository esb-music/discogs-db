class CreateAlbums < ActiveRecord::Migration[7.0]
  def change
    create_table :albums do |t|
      t.belongs_to :discog, null: false, foreign_key: true
      t.string :title, null: false
      t.string :subtitle
      t.string :img
      t.string :alt
      t.string :wiki
      t.string :label
      t.string :released
      t.string :recorded
      t.string :producer
      t.string :venue
      t.string :length

      t.timestamps
    end

  end
end
