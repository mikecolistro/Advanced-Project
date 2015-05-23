#This model stores information retrieved from the department table in the database.
class Department

	attr_reader :id
	attr_reader :name
	attr_reader :code
	attr_reader :courses

	def initialize(hash)
	    @id = hash["id"]
	    @name = hash["name"]
	    @code = hash["code"]
	    @courses = hash["courses"]
	end

end
