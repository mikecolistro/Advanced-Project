#This model stores information retrieved from the course table in the database.
class Course

  attr_reader :id
  attr_reader :title
  attr_reader :code
  attr_reader :section
  attr_reader :department_name
  attr_reader :instructor
  attr_reader :term

  def initialize(hash)
    @id = hash["id"]
    @title = hash["title"]
    @code = hash["code"]
    @section = hash["section"]
    @department_name = hash["department_name"]
    @instructor = hash["instructor"]
    @term = hash["term"]
  end

end
