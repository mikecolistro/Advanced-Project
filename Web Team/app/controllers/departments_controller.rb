require 'net/http'
require 'json'

#This controller handles all methods related to departments.
class DepartmentsController < ApplicationController

	before_action :confirm_logged_in, :except => [:index]

  #Displays a list of all of the departments.
  #@return [Array<Department>]List of departments
  def index
	    uri = URI("http://107.170.7.58:4567/api/department")
	    parameters = {"ext" => "json", "count" => "1000"}
	    response = Net::HTTP.post_form(uri, parameters)

	    list = JSON.parse(response.body)
	    @departments = Array.new
	    list.each do |department|
	    	@departments.push Department.new(department['data'])
	    end
	    # End list

	end
	# End index

end
# End DepartmentsController























