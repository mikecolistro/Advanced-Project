# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140219213304) do

  create_table "books", force: true do |t|
    t.string   "title"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "buys", force: true do |t|
    t.integer  "user_id"
    t.integer  "edition_id"
    t.datetime "start_date"
    t.datetime "end_date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "buys", ["edition_id"], name: "index_buys_on_edition_id"
  add_index "buys", ["user_id"], name: "index_buys_on_user_id"

  create_table "contacts", force: true do |t|
    t.integer  "buyer_id"
    t.integer  "seller_id"
    t.integer  "listing_id"
    t.datetime "date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "course_books", id: false, force: true do |t|
    t.integer  "course_id"
    t.integer  "edition_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "course_books", ["course_id"], name: "index_course_books_on_course_id"
  add_index "course_books", ["edition_id"], name: "index_course_books_on_edition_id"

  create_table "courses", force: true do |t|
    t.string   "title"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "code"
    t.string   "section"
    t.integer  "department_id"
    t.string   "instructor"
    t.string   "term"
  end

  add_index "courses", ["department_id"], name: "index_courses_on_department_id"

  create_table "departments", force: true do |t|
    t.string   "name"
    t.string   "code"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "editions", force: true do |t|
    t.integer  "book_id"
    t.integer  "isbn"
    t.integer  "edition_num"
    t.string   "author"
    t.string   "image"
    t.string   "publisher"
    t.string   "cover"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "editions", ["book_id"], name: "index_editions_on_book_id"

  create_table "sells", force: true do |t|
    t.integer  "user_id"
    t.integer  "edition_id"
    t.integer  "price"
    t.datetime "start_date"
    t.datetime "end_date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "sells", ["edition_id"], name: "index_sells_on_edition_id"
  add_index "sells", ["user_id"], name: "index_sells_on_user_id"

  create_table "users", force: true do |t|
    t.string   "email"
    t.boolean  "verified",        default: false
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "password_digest"
  end

  create_table "verifications", id: false, force: true do |t|
    t.string   "code"
    t.integer  "user_id"
    t.datetime "end_date"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "verifications", ["user_id"], name: "index_verifications_on_user_id"

end
