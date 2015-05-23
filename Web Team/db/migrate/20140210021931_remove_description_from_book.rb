class RemoveDescriptionFromBook < ActiveRecord::Migration
  def change
    remove_column :books, :description, :text
  end
end
