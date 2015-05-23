class AddEditionToBook < ActiveRecord::Migration
  def change
    add_column :books, :edition, :integer
  end
end
