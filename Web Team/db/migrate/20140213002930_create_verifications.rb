class CreateVerifications < ActiveRecord::Migration
  def change
    create_table :verifications, :primary_key => :code do |t|
      t.integer :user_id
      t.datetime :end_date

      t.timestamps
    end
    change_column(:verifications, :code, :string)
    add_index(:verifications, :user_id)
  end
end
