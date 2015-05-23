class CreateEditions < ActiveRecord::Migration

  def change
  create_table :editions do |t|
    t.integer :book_id
    t.integer :isbn
    t.integer :edition_num
    t.string :author
    t.string :image
    t.string :publisher
    t.string :cover

    t.timestamps
  end
    add_index(:editions, :book_id)
  end

end
