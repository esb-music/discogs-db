class CreateDiscogs < ActiveRecord::Migration[7.0]
  def change
    create_table :discogs do |t|
      t.string :key, null: false
      t.string :name, null: false
      t.string :img, null: false
      t.string :alt, null: false
      t.string :wiki
      t.integer :active, null: false

      t.timestamps
    end
    add_index :discogs, :key, unique: true
    add_check_constraint :discogs, "active in (-1, 0, 1)", name: "active_check"
  end
end
