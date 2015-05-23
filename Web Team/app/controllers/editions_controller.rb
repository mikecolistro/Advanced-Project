require 'net/http'
require 'json'

#This controller handles all methods related to books.
class EditionsController < ApplicationController
  before_action :confirm_logged_in, :except => [:index]

  #Displays a list of all of the books currently in the database.
  #Filtering enables the user to display only a list of books belongs to a certain department.
  #@return [Array<Edition>]List of books
  def index
    uri = URI("http://107.170.7.58:4567/api/department")
    parameters = {"ext" => "json", "count" => "1000"}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)
    @departments = Array.new
    list.each do |department|
      @departments.push Department.new(department['data'])
    end

    page = params[:page]
    offset = page.to_s+"0"
    @prevPage = page.to_i-1
    @nextPage = page.to_i+1

    if params[:department]
      department = params[:department]
      @departmentName = @departments[department.to_i-1].name

      parameters = {"ext" => "json", "count" => "10", "offset" => offset, "department_id" => department}
    else
      parameters = {"ext" => "json", "count" => "10", "offset" => offset}
    end

    uri = URI("http://107.170.7.58:4567/api/book")
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)

    @editions = Array.new
    list.each do |book|
      @editions.push Edition.new(book["data"])
    end
  end
# End index

  #Displays the profile page of a book, including all of the current sale listings for that book.
  #@return [Edition] A book
  def show
    page = params[:page]
    offset = page.to_s+"0"
    @prevPage = page.to_i-1
    @nextPage = page.to_i+1

    id = params[:id]
    uri = URI("http://107.170.7.58:4567/api/book")
    parameters = {"ext" => "json", "id" => id.to_s}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)
    @edition = Edition.new(list[0]["data"])

    uri = URI("http://107.170.7.58:4567/api/sell")
    parameters = {"ext" => "json", "edition_id" => id.to_s, "count" => "10", "offset" => offset}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)

    @availableCopies = Array.new
    list.each do |listing|
      if listing["kind"].eql? "sell"
        @availableCopies.push Sell.new(listing["data"])
      end
    end
  end
# End show

  # Returns all data and filter by title.
  def search

    page = params[:page]
    offset = page.to_s+"0"
    @prevPage = page.to_i-1
    @nextPage = page.to_i+1

    uri = URI("http://107.170.7.58:4567/api/book")
    parameters = {"ext" => "json", "count" => "1000000000", "offset" => offset}
    response = Net::HTTP.post_form(uri, parameters)
    list = JSON.parse(response.body)

    puts list

    # Text input by user
    @query = params[:q]
    if @query
      @query = @query.split(' ') 

      # Attribute the user selected to search by
      # attr_selected = params[:search_for_attribute]
      attr_selected = 'title'
      puts "ATTR: #{attr_selected}"
      puts list.first['data'][attr_selected]
      

      @editions = Array.new

      # If any word in searched query is present in book's title then the book is returned
      list.each do |book|
          @query.each do |keyword|
            @editions.push Edition.new(book["data"]) if book['data'][attr_selected].include?(keyword)
          end
      end
    end
  end


end