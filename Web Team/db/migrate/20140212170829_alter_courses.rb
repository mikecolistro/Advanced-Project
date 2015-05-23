class AlterCourses < ActiveRecord::Migration

  def change
    remove_column("courses", "description", "text")
    add_column("courses", "code", "string")
    add_column("courses", "section", "string")
    add_column("courses", "department_id", "integer")
    add_column("courses", "instructor", "string")
    add_column("courses", "term", "string")
    add_index("courses", "department_id")
  end

end
