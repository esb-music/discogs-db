# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# This file is the source Rails uses to define your schema when running `bin/rails
# db:schema:load`. When creating a new database, `bin/rails db:schema:load` tends to
# be faster and is potentially less error prone than running all of your
# migrations from scratch. Old migrations may fail to apply correctly if those
# migrations use external dependencies or application code.
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema[7.0].define(version: 2023_12_02_130756) do
  create_table "albums", force: :cascade do |t|
    t.integer "discog_id", null: false
    t.string "title", null: false
    t.string "subtitle"
    t.string "img"
    t.string "alt"
    t.string "wiki"
    t.string "label"
    t.string "released"
    t.string "recorded"
    t.string "producer"
    t.string "venue"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.string "length"
    t.string "comment"
    t.index ["discog_id"], name: "index_albums_on_discog_id"
  end

  create_table "discogs", force: :cascade do |t|
    t.string "key", null: false
    t.string "name", null: false
    t.string "img", null: false
    t.string "alt", null: false
    t.string "wiki"
    t.integer "active", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["key"], name: "index_discogs_on_key", unique: true
    t.check_constraint "active in (-1, 0, 1)", name: "active_check"
  end

  create_table "musicians", force: :cascade do |t|
    t.string "name", null: false
    t.string "info"
    t.string "img", null: false
    t.string "alt", null: false
    t.string "wiki"
    t.date "bdate"
    t.date "ddate"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "performings", force: :cascade do |t|
    t.integer "track_id", null: false
    t.integer "musician_id", null: false
    t.string "instruments"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.index ["musician_id"], name: "index_performings_on_musician_id"
    t.index ["track_id"], name: "index_performings_on_track_id"
  end

  create_table "tracks", force: :cascade do |t|
    t.integer "discog_id", null: false
    t.integer "album_id", null: false
    t.integer "tno", null: false
    t.string "title", null: false
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
    t.string "length"
    t.index ["album_id"], name: "index_tracks_on_album_id"
    t.index ["discog_id"], name: "index_tracks_on_discog_id"
  end

  add_foreign_key "albums", "discogs"
  add_foreign_key "performings", "musicians"
  add_foreign_key "performings", "tracks"
  add_foreign_key "tracks", "albums"
  add_foreign_key "tracks", "discogs"
end
