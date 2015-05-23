class CreateContacts < ActiveRecord::Migration
  def change
    create_table :contacts do |t|
      t.integer :buyer_id
      t.integer :seller_id
      t.integer :listing_id
      t.datetime :date

      t.timestamps
    end
  end
end
