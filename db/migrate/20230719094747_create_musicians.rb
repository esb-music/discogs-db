class CreateMusicians < ActiveRecord::Migration[7.0]
  def change
    create_table :musicians do |t|
      t.string :name, null: false
      t.string :info
      t.string :img, null: false
      t.string :alt, null: false
      t.string :wiki
      t.date   :bdate
      t.date   :ddate

      t.timestamps
    end
  end
end
