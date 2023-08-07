class CreateTracks < ActiveRecord::Migration[7.0]
  def change
    create_table :tracks do |t|
      t.belongs_to :discog, null: false, foreign_key: true
      t.belongs_to :album, null: false, foreign_key: true
      t.integer :tno, null: false
      t.string  :title, null: false
      t.decimal :length, precision: 6, scale: 2

      t.timestamps
    end
  end
end
