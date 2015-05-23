class CreateSells < ActiveRecord::Migration
  def change
    create_table :sells do |t|
      t.integer :user_id
      t.integer :edition_id
      t.integer :price
      t.datetime :start_date
      t.datetime :end_date

      t.timestamps
    end
    add_index(:sells, :user_id)
    add_index(:sells, :edition_id)
  end
end
