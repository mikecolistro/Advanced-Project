class CreateBuys < ActiveRecord::Migration
  def change
    create_table :buys do |t|
      t.integer :user_id
      t.integer :edition_id
      t.datetime :start_date
      t.datetime :end_date

      t.timestamps
    end
    add_index(:buys, :user_id)
    add_index(:buys, :edition_id)
  end
end
