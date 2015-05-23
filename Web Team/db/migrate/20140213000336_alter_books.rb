class AlterBooks < ActiveRecord::Migration
  def change
    remove_column("books", "author", "string")
    remove_column("books", "isbn", "integer")
    remove_column("books", "edition", "integer")
    remove_column("books", "image", "string")
  end
end
