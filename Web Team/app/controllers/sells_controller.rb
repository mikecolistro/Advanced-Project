require 'net/http'
require 'json'

#This controller handles all methods related to sell listings.
class SellsController < ApplicationController

  before_action :confirm_logged_in

  #Displays a list of all of the current sale listings in the database.
  #Filtering enables the user to display only a list of sell listings for a certain department.
  #@return [Array<Sell>] List of sell listings.
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

    uri = URI("http://107.170.7.58:4567/api/sell")
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)

    @sells = Array.new
    @books = Array.new
    list.each do |listing|
      if listing["kind"].eql? "sell"
        @sells.push Sell.new(listing["data"])
      else if listing["kind"].eql? "book"
             @books[listing["data"]["id"]] = Edition.new(listing["data"])
           end
      end
    end
  end
  # End index

  #Displays the form page for creating a new sale listing for a book.
  #The isbn is used to search for the book in the database and populate the form with the relevant information.
  def new
    isbn = params[:isbn]
    uri = URI("http://107.170.7.58:4567/api/book")
    parameters = {"ext" => "json", "isbn" => isbn.to_s}
    response = Net::HTTP.post_form(uri, parameters)
    list = JSON.parse(response.body)

    if list.empty?
      redirect_to(action: "search", error: isbn.to_s)
    else
      @edition = Edition.new(list[0]["data"])
    end
  end
  # End new

  #Create a new sell listing.
  def create
    userID = session[:user_id]
    editionID = params[:edition_id]
    price = params[:price]

    uri = URI("http://107.170.7.58:4567/api/create/sell")
    parameters = {"ext" => "json", "user_id" => userID, "edition_id" => editionID, "price" => price, "start_date" => Time.now, "end_date" => 90.days.from_now}
    response = Net::HTTP.post_form(uri, parameters)
    list = JSON.parse(response.body)

    @response = list[0]["kind"]
  end
  # End create

  #Searches the database for a book matching this isbn and populates the form fields with it's info if a match is found.
  #Otherwise it returns an error.
  def search
    @error = params[:error]
  end

  #Displays a list of the current user's sell listings.
  def my_listings
    page = params[:page]
    offset = page.to_s+"0"
    @prevPage = page.to_i-1
    @nextPage = page.to_i+1

    userID = session[:user_id]

    uri = URI("http://107.170.7.58:4567/api/sell")
    parameters = {"ext" => "json", "user_id" => userID, "count" => "10", "offset" => offset}
    response = Net::HTTP.post_form(uri, parameters)

    list = JSON.parse(response.body)

    @sells = Array.new
    @books = Array.new
    list.each do |listing|
      if listing["kind"].eql? "sell"
        @sells.push Sell.new(listing["data"])
      else if listing["kind"].eql? "book"
             @books[listing["data"]["id"]] = Edition.new(listing["data"])
           end
      end
    end
  end

  #Delete a sell listing.
  def delete
    sellID = params[:sell_id]

    uri = URI("http://107.170.7.58:4567/api/delete/sell")
    parameters = {"ext" => "json", "id" => sellID}
    response = Net::HTTP.post_form(uri, parameters)
    list = JSON.parse(response.body)

    @response = list[0]["kind"]
  end

  #Send a email to the seller indicating the current user would like to buy their book.
  def contactSeller
    userID = session[:user_id]
    sellID = params[:sell_id]

    uri = URI("http://107.170.7.58:4567/api/contact/sell")
    parameters = {"ext" => "json", "user_id" => userID, "sell_id" => sellID}
    response = Net::HTTP.post_form(uri, parameters)
    list = JSON.parse(response.body)

    @response = list[0]["kind"]
  end

end