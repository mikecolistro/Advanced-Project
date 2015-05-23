#This model stores information retrieved from the sell table in the database.
class Sell

  attr_reader :id
  attr_reader :user_id
  attr_reader :edition_id
  attr_reader :price
  attr_reader :start_date
  attr_reader :end_date

  def initialize(hash)
    @id = hash["id"]
    @user_id = hash["user_id"]
    @edition_id = hash["edition_id"]
    @price = hash["price"]
    @start_date = DateTime.iso8601(hash["start_date"])
    @end_date = DateTime.iso8601(hash["end_date"])
  end

end
