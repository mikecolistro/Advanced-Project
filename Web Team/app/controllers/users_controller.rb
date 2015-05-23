require 'net/http'

#This controller handles all methods related to users.
class UsersController < ApplicationController

#Displays a login screen
  def login
  end
  #End login
#Displays a signup screen
def signup
end
  #End signup

  #Handles the login process.
  #@return a session if successful
  def attempt_login
    if params[:email].present? && params[:password].present?
      email= params[:email] + "@lakeheadu.ca"
      password= params[:password]

      uri = URI("http://107.170.7.58:4567/api/login")
      parameters = {"ext" => "json", "email" => email, "password" => password }
      response = Net::HTTP.post_form(uri, parameters)


      list= JSON.parse(response.body)
      if list[0]["kind"] == "error"
        redirect_to(action: "login")
        flash[:alert] = "Email and password do not match"
      else
        session[:user_id]= list[0]["data"]["token"]
        session[:email] = email
        session[:password]= password
        redirect_to(editions_path)

        flash[:notice]= "You are now logged in"
      end
    else
      redirect_to(action: "login")
      flash[:alert]= "Fill in all fields"
    end
  end
  #End attempt_login

  #Creates a new user if successful
  def create
    if params[:user][:email].present? && params[:user][:password].present?
      if params[:user][:password] == params[:user][:confirm_password]
      email = params[:user][:email] + "@lakeheadu.ca"
      password = params[:user][:password]

      uri = URI("http://107.170.7.58:4567/api/create/user")
      parameters = {"ext" => "json", "email" => email, "password" => password}
      res = Net::HTTP.post_form(uri, parameters)
      flash[:notice]= "Account created"
      render(action: "login")
      else
        flash[:alert]= "Passwords do not match"
        render(action: "signup")
      end
    else
      flash[:notice]= "Please fill in all fields"
      render(action: "signup")
    end
  end
  #End create
  #Creates a new password for the user currently logged in
  def change_password
    if params[:password].present? && params[:confirm_password].present?
      if params[:password] == params[:confirm_password]
        password = params[:password]
        uri = URI("http://107.170.7.58:4567/api/edit/password")
        parameters = {"email" => session[:email], "currentpassword" => session[:password], "newpassword" => password}
        res = Net::HTTP.post_form(uri, parameters)
        puts res.body
        redirect_to(editions_path)
        flash[:notice]= "Successfully changed the password"
      else
        flash[:alert]= "Passwords do not match"
      end
    else
      flash[:notice] = "Please fill in all fields"
    end
  end
  #End change_password
  #Handles logout process
  def logout
    session[:user_id] = nil
    session[:email] = nil
    session[:password]=nil
    flash[:notice] = "Logged out"
    redirect_to(:action => 'login')
  end
#End logout

end