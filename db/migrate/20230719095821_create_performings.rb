class CreatePerformings < ActiveRecord::Migration[7.0]
  def change
    create_table :performings do |t|
      t.belongs_to :track,    null: false, foreign_key: true
      t.belongs_to :musician, null: false, foreign_key: true
      t.string     :instruments

      t.timestamps
    end
  end
end
