require 'i18n'
require 'json'
require 'sinatra/activerecord'
require 'sinatra/base'
require_relative './db_handler'
require_relative './mailhandler'
require_relative './serializer'
require_relative './search'

# Handles all url pattern matching and sends the requested data back to the user. Main entry point of the application.
class Routes < Sinatra::Base
  @extensions = Serializer::EXTENSIONS
  I18n.enforce_available_locales = false

  # Checks for existence of the ext get parameter and assigns it to a class variable
  # @param p [Hash] the parameters hash
  # Returns the parameters hash without the ext parameter
  def clean_extension(p)
    @@ext = "json" 
    return p unless p.key?("ext")
    @@ext = p["ext"]
    p.delete("ext")
    return p
  end

  # Routes

  # @method get_documentation
  # @overload get "/"
  # Returns the documentation if your have a top-level request
  get '/' do
    index = File.expand_path('doc/index.html', settings.public_folder)
    if File.exist?(index)
      redirect to('doc/index.html')
    else
      "YARD Docs not generated! Please contact the server administrators."
    end
  end

  # @method login
  # @overload post "/api/login"
  # @param email [String] email of user
  # @param password [String] unhashed password
  # Returns an access token of the user if login succeeded, null if failed
  post "/api/login" do
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")

    email = URI.unescape(parameters["email"])
    password = URI.unescape(parameters["password"])

    tok = DBHandler.generate_access_token(email) if(DBHandler.login(email, password))

    unless tok.nil? then
      token_hash = {"token" => tok}
      data_hash = {"token" => [token_hash]}
    else
      error_hash = {"error" => "Wrong username/password or user has not completed verification process."}
      data_hash = {"error" => [error_hash]}
    end

    Serializer.serialize(data_hash, @@ext)
  end

  # @method select_from_database
  # @overload post "/api/:type"
  # @param type [String] The type of record being fetched
  # @param post-params [Hash] the POST parameters passed with the HTTP request, gets passed as Hash to database
  # Returns the record you're searching for or returns empty hash in JSON/XML
  post "/api/:type" do
    type = params[:type]
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"
    count = parameters.key?("count") ? parameters.delete("count") : 1
    offset = parameters.key?("offset") ? parameters.delete("offset") : 0

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")
  
    if (parameters.has_key?("user_id"))
      token = {"token" => parameters["user_id"]}
      if (DBHandler.verify_access_token(token))
        parameters["user_id"] = DBHandler.convert_token_to_user_id(token)
      else
        error_hash = {"error" => "Invalid access token"}
        data_hash = {"error" => error_hash}
        return Serializer.serialize(data_hash, @@ext)
      end
    end

    case type
    when "book"
      books = DBHandler.get_books(parameters, count, offset)
      data_hash = {"book" => books}
    when "department"
      departments = DBHandler.get_departments(parameters, count, offset)
      data_hash = {"department" => departments}
    when "departmentdetail"
      departments = DBHandler.get_departments(parameters, count, offset)
      departments.each do |department|
        courses = DBHandler.get_courses({"department_id" => department["id"]}, 1000, 0)
        department["courses"] = courses
      end
      data_hash = {"department_courses" => departments}
    when "course"
      courses = DBHandler.get_courses(parameters, count, offset)
      data_hash = {"course" => courses}
    when "coursedetail"
      courses = DBHandler.get_courses(parameters, count, offset)
      books = DBHandler.get_books({"course_book.course_id" => courses[0]["id"]}, 100, 0)
      data_hash = {"course" => courses, "book" => books}
    when "sell"
      sells = DBHandler.get_sells(parameters, count, offset)
      edition_ids = sells.collect { |sell| sell["edition_id"] }
      books = DBHandler.get_books({"id" => edition_ids}, edition_ids.count, 0)
      data_hash = {"sell" => sells, "book" => books}
    when "counts"
      counts = DBHandler.get_counts
      data_hash = {"counts" => counts}
    else
      error_hash = {"error" => "Invalid data type requested."}
      data_hash = {"error" => error_hash}
    end

    Serializer.serialize(data_hash, @@ext)
  end

  # @method add_to_database
  # @overload post "/api/create/:type"
  # @param type [String] the type of record being added
  # @param post-params [Hash] the POST parameters passed with the HTTP request, gets passed as Hash to database
  # Returns the record you added to the database as your selected extension
  post "/api/create/:type" do
    type = params[:type]
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")

    if (parameters.has_key?("user_id"))
      token = {"token" => parameters["user_id"]}
      if (DBHandler.verify_access_token(token))
        parameters["user_id"] = DBHandler.convert_token_to_user_id(token)
      else
        error_hash = {"error" => "Invalid access token"}
        data_hash = {"error" => error_hash}
        return Serializer.serialize(data_hash, @@ext)
      end
    end

    case type
    when "sell"
      sells = DBHandler.create_sell(parameters)
      data_hash = {"sell" => sells}
    when "user"
      parameters['email'] = URI.unescape(parameters['email']) if parameters.has_key?('email')
      parameters['password'] = URI.unescape(parameters['password']) if parameters.has_key?('password')
      user = DBHandler.create_user(parameters, self.class.production?)
      if user[0]['error'] == nil
        kind = 'user'
      else
        kind = 'error'
      end
      data_hash = { kind => user }
    else
      error_hash = {"error" => "Invalid data type requested."}
      data_hash = {"error" => error_hash}    
    end

    Serializer.serialize(data_hash, @@ext)
  end

  # @method delete_from_database
  # @overload post "/api/delete/:type"
  # @param type [String] the type of record being added
  # @param post-params [Hash] the POST parameters passed with the HTTP request, gets passed as Hash to database
  # Returns the record you added to the database as your selected extension
  post '/api/delete/:type' do
    type = params[:type]
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")

    if (parameters.has_key?("user_id"))
      token = {"token" => parameters["user_id"]}
      if (DBHandler.verify_access_token(token))
        parameters["user_id"] = DBHandler.convert_token_to_user_id(token)
      else
        error_hash = {"error" => "Invalid access token"}
        data_hash = {"error" => error_hash}
        return Serializer.serialize(data_hash, @@ext)
      end
    else
      error_hash = {"error" => "Invalid access token"}
      data_hash = {"error" => error_hash}
      return Serializer.serialize(data_hash, @@ext)
    end

    case type
    when "sell"
      sell = DBHandler.delete_sell(parameters)
      data_hash = {"sell" => sell}
    else
      error_hash = {"error" => "Invalid data type requested."}
      data_hash = {"error" => error_hash}
    end

    Serializer.serialize(data_hash, @@ext)
  end

  # @method edit_from_database
  # @overload post "/api/edit/:type"
  # @param type [String] the type of record to edit
  # @param post-params [Hash] the POST parameters passed with the HTTP request, gets passed as Hash to database
  # Returns the edited record in selected extension
  post '/api/edit/:type' do
    type = params[:type]
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")

    case type
    when "sell"
      sell = DBHandler.edit_sell(parameters)
      if sell.nil? then
        error_hash = {"error" => "Sell couldn't be found or price wasn't included in parameters."}
        data_hash = {"error" => error_hash}
      end
      data_hash = {"sell" => sell}
    when "password"
      # Check for required parameters
      if (!parameters.has_key?("email") or
        !parameters.has_key?("currentpassword") or
        !parameters.has_key?("newpassword")) then
        data_hash = {"error" => "Email/currentpassword/newpassword not included."}
      else
        result = DBHandler.change_password(parameters["email"],
          parameters["currentpassword"],
          parameters["newpassword"])
        data_hash = {"result" => result}
      end
    else
      error_hash = {"error" => "Invalid data type requested."}
      data_hash = {"error" => error_hash}
    end

    Serializer.serialize(data_hash, @@ext)
  end

  # @method search
  # @overload post '/api/search/:type'
  # @param type [String] The type of record to search
  # @param post-params [Hash] Post parameters
  # Returns a list of entries matching the passed string to search for using LIKE *foo*
  post '/api/search/:type' do
    type = params[:type]
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    # Remove captures, type and splat from outgoing hash
    parameters.delete("captures")
    parameters.delete("type")
    parameters.delete("splat")

    search_string = params["searchstring"]

    case type
    when "book"
      books = DBHandler.search(search_string, Search.tables[:book])
      data_hash = {"book" => books}
    when "course"
      courses = DBHandler.search(search_string, Search.tables[:course])
      data_hash = {"course" => courses}
    when "department"
      departments = DBHandler.search(search_string, Search.tables[:department])
      data_hash = {"department" => departments}
    else
      error = "Invalid data type requested."
      data_hash = {"error" => error}
    end
    Serializer.serialize(data_hash, @@ext)
  end

  # @method verify
  # @param code [String] code to verify account
  # Returns a message saying if the verification was successful or not
  get '/api/verify/:code' do
    result = DBHandler.verify_user(params[:code])
    if result == true
      return "Verification successful"
    else
      return "Error validating account"
    end
  end

  # @method contact/sell
  # Sends an email request to buy a book
  post '/api/contact/sell' do
    parameters = clean_extension(params)
    parameters = Serializer.parse_json_parameters(parameters["json"]) if parameters.has_key? "json"

    if (parameters.has_key?("user_id"))
      token = {"token" => parameters["user_id"]}
      if (DBHandler.verify_access_token(token))
        user_id = DBHandler.convert_token_to_user_id(token)
        buyer_email = DBHandler.get_user_email(user_id)
        sell = DBHandler.get_sells({"id" => parameters["sell_id"]}, 1, 0)
        edition_id = sell[0]['edition_id'] unless sell == []
        book = DBHandler.get_books({"id" => edition_id}, 1, 0)
        book_title = book[0]['title'] unless book == []
        MailHandler.send_buy_request(buyer_email, book_title) if self.class.production?
        success_hash = {"success" => "Email sent"}
        data_hash = {"success" => [success_hash]}
        return Serializer.serialize(data_hash, @@ext)
      else
        error_hash = {"error" => "Invalid access token"}
        data_hash = {"error" => [error_hash]}
        return Serializer.serialize(data_hash, @@ext)
      end
    end
  end
end
