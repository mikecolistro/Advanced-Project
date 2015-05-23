require 'json'
require 'net/http'
require 'rack/test'
require 'test/unit'
require_relative '../src/routes'
require_relative './fixtures'

# Main class for running unit tests
class SinatraTest < Test::Unit::TestCase
  include Rack::Test::Methods
  @db_modified = false

  # Sets up the Routes app to be run
  def app
    routes = Routes
    routes.set :public_folder, 'public'
    routes.set :environment, :test
    routes
  end

  # Code to run before each test
  def setup
    DBHandler.establish_test_connection
  end

  def teardown
    if @db_modified
      Fixtures.load
      @db_modified = false
    end
  end

end
