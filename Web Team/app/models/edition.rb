#This model stores information retrieved from the Edition table in the database.
class Edition

  attr_reader :id
  attr_reader :isbn
  attr_reader :book_id
  attr_reader :author
  attr_reader :edition_num
  attr_reader :publisher
  attr_reader :cover
  attr_reader :image
  attr_reader :title
  attr_reader :for_sale
  attr_reader :course_code

  # searchable do
  #   text :title
  # end

  def initialize(hash)
    @id = hash["id"]
    @isbn = hash["isbn"]
    @book_id = hash["book_id"]
    @author = hash["author"]
    @edition_num = hash["edition"]
    @publisher = hash["publisher"]
    @cover = hash["cover"]
    @image = hash["image"]
    @title = hash["title"]
    @for_sale = hash["for_sale"]
    @course_code = hash["course_code"]
  end

end