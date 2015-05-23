class CreateCourseBooks < ActiveRecord::Migration
  def change
    create_table :course_books, {:id => false} do |t|
      t.integer :course_id
      t.integer :edition_id

      t.timestamps
    end
    add_index(:course_books, :course_id)
    add_index(:course_books, :edition_id)
  end
end
