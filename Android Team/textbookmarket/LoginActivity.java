package com.lakehead.textbookmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * This OnTaskCompleted is an interface that Activities receiving callbacks from async tasks should implement.
 */
public class LoginActivity extends Activity /*implements OnTaskCompleted*/ {
    String deptCode;
    JSONArray jArray;

    EditText emailText;
    EditText passText;

    Button loginButton;
    Button toRegButton;

    SharedPreferences prefs;
    ProgressBar bar;

    String tokenString;
    JSONObject rememberToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = (EditText) findViewById(R.id.email_field);
        passText = (EditText) findViewById(R.id.pass_field);
        //necessary for some reason to prevent the password field fonts from defaulting to courier
        passText.setTypeface(Typeface.DEFAULT);
        passText.setTransformationMethod(new PasswordTransformationMethod());

        loginButton = (Button) findViewById(R.id.login_button);
        toRegButton = (Button) findViewById(R.id.no_account_button);

        prefs = this.getSharedPreferences("com.lakehead.textbookmarket", Context.MODE_PRIVATE);

        // Keep this here for testing logging in and registering
        //prefs.edit().clear().commit();

        //tokenString = prefs.getString("remember_token","");
        //if(!tokenString.equals(""))
        //{
        //    goToMyBooks();
        //}
        bar = (ProgressBar) findViewById(R.id.loader);
        bar.setVisibility(View.INVISIBLE);


    }

    //Get the login info, encode the email address, and then put the info into a String.

    /**
     * Executes a LoginTask
     * @param v
     */
    public void login(View v)
    {
        String emailAddress = emailText.getText().toString();
        String password = passText.getText().toString();

        if(emailAddress.equals("") || password.equals(""))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid email address or password", Toast.LENGTH_SHORT);
            toast.show();
            passText.setText("");
        }
        else
        {
            try
            {
                emailAddress = URLEncoder.encode(emailAddress, "utf-8");
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            String url = "http://lakehead-books.herokuapp.com/api/v1/login";

            hideUI();
            //new LoginTask(this).execute(url, emailAddress, password);
        }
    }

    public void goToRegister(View v)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void goToMyBooks()
    {
        //Intent intent = new Intent(LoginActivity.this, MyBooksActivity.class);
        //startActivity(intent);
        //finish();
    }

    /*
    This is a callback from the async task. We'll override this later

    @Override
    public void onTaskCompleted(Object obj)
    {
        if(obj == null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Internal server error", Toast.LENGTH_SHORT);
            toast.show();
            showUI();
            passText.setText("");
        }
        else if(obj.getClass() == JSONObject.class)
        {
            this.rememberToken = (JSONObject)obj;
            String token = "";
            String emailAddress = "";
            try
            {
                token = rememberToken.getString("token");
            }
            catch(JSONException e)
            {
                Log.d("Exceptions", e.toString());
            }

            emailAddress = emailText.getText().toString();

            prefs.edit().putString("remember_token", token).commit();
            prefs.edit().putString("email_address", emailAddress).commit();
            bar.setVisibility(View.GONE);
            goToMyBooks();
        }
        else if(obj.getClass() == String.class)
        {

            if(obj.equals(LoginTask.MESSAGE_UNAUTHORIZED))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT);
                toast.show();
                showUI();
                passText.setText("");
            }
            else if(obj.equals(LoginTask.MESSAGE_CONNECTION_PROBLEM))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Internet connection problem", Toast.LENGTH_SHORT);
                toast.show();
                showUI();
            }

        }
    }
    */

    /**
     * A simple function used to hide the user interface while executing a background task.
     */
    private void hideUI()
    {
        emailText.setVisibility(View.INVISIBLE);
        passText.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        toRegButton.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
    }

    /**
     * A simple function used to show the user interface after executing a background task.
     */
    private void showUI()
    {
        emailText.setVisibility(View.VISIBLE);
        passText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        toRegButton.setVisibility(View.VISIBLE);
        bar.setVisibility(View.GONE);
    }
}