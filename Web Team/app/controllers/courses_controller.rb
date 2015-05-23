require 'net/http'
require 'json'

#This controller handles all methods related to courses.
class CoursesController < ApplicationController
  before_action :confirm_logged_in, :except => [:index]

  #Displays a list of all of the courses that belong to a particular department.
  #@return [Array<Course>]List of courses
  def index
    department_id = params[:department_id]
    

    uri = URI("http://107.170.7.58:4567/api/course")
    parameters = {"ext" => "json", "count" => "1000", "department_id" => department_id}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)

    @courses = Array.new
    list.each do |course|
      @courses.push Course.new(course['data'])
    end
    # End list
    @courses = @courses.sort_by{ |course| course.code }

  end
  # End index

  #Display the profile page of a course, including all of the books needed for that course.
  #return [Course] A course.
  def show
    id = params[:id]
    uri = URI("http://107.170.7.58:4567/api/coursedetail")
    parameters = {"ext" => "json", "id" => id.to_s}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)
    @course = Course.new(list[0]["data"])

    @books = Array.new
    list.each do |book|
      if book["kind"].eql? "book"
        @books.push Edition.new(book["data"])
      end
    end
  end

end
