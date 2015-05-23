package com.lakehead.textbookmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Activity used for user Registration.
 */
public class RegisterActivity extends Activity /*implements OnTaskCompleted*/ {
    String deptCode;
    JSONArray jArray;

    EditText emailText;
    EditText userText;
    EditText passText;
    EditText passConfText;
    Button registerButton;
    Button toLoginButton;

    String email;
    String user;
    String pass;
    String passConfirm;

    ProgressBar bar;

    JSONObject rememberToken;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_register);


        emailText = (EditText) findViewById(R.id.email_register_field);
        userText = (EditText) findViewById(R.id.user_register_field);
        passText = (EditText) findViewById(R.id.register_pass_field);
        passText.setTypeface(Typeface.DEFAULT);
        passText.setTransformationMethod(new PasswordTransformationMethod());
        passConfText = (EditText) findViewById(R.id.register_confirm_pass_field);
        passConfText.setTypeface(Typeface.DEFAULT);
        passConfText.setTransformationMethod(new PasswordTransformationMethod());

        registerButton = (Button) findViewById(R.id.register_button);
        toLoginButton = (Button) findViewById(R.id.already_account_button);

        bar = (ProgressBar) findViewById(R.id.loader);
        bar.setVisibility(View.INVISIBLE);

        prefs = this.getSharedPreferences("com.lakehead.textbookmarket", Context.MODE_PRIVATE);

    }

    /**
     * Executes a RegisterTask
     * @param v
     */
    public void register(View v)
    {
        email = emailText.getText().toString();
        user = userText.getText().toString();
        pass = passText.getText().toString();
        passConfirm = passConfText.getText().toString();

        //email = "test123@gmail.com";
        //user = "test123";
        //pass = "qwerty";
        //passConfirm = "qwerty";


        if(!validEmail(email))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(!user.matches("[A-Za-z0-9_-]*"))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Valid username characters include letters, numbers, _, and -", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(!pass.equals(passConfirm))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT);
            toast.show();
            passText.setText("");
            passConfText.setText("");
        }
        else
        {
            String url = "http://lakehead-books.herokuapp.com/api/v1/register";
            //new RegisterTask(this).execute(url, email, user, pass, passConfirm);
            hideUI();
        }
    }

    public void goToLogin(View v)
    {
        finishActivity();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    /*

    @Override
    public void onTaskCompleted(Object obj)
    {
        if(obj == null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Server Error. Could not register.", Toast.LENGTH_SHORT);
            toast.show();
            showUI();
            //passText.setText("");
            //passConfText.setText("");
            showUI();
        }
        else if(obj.getClass() == String.class)
        {
            if(obj.equals(RegisterTask.USER_OR_EMAIL_ALREADY_EXISTS))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "A user with that email address or username already exists.", Toast.LENGTH_SHORT);
                toast.show();
                showUI();
            }
            if(obj.equals(RegisterTask.MESSAGE_CONNECTION_PROBLEM))
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Internet connection problem", Toast.LENGTH_SHORT);
                toast.show();
                showUI();
            }

        }
        else
        {
            this.rememberToken = (JSONObject)obj;
            String token = "";

            try
            {
                token = rememberToken.getString("token");
            }
            catch(JSONException e)
            {
                Log.d("Exceptions", e.toString());
            }

            prefs.edit().putString("remember_token", token).commit();
            prefs.edit().putString("email_address", email).commit();
            bar.setVisibility(View.GONE);
            goToAccount();
        }
    }
    */

    private void finishActivity()
    {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    /**
     * A simple function used to hide the user interface while executing a background task.
     */
    private void hideUI()
    {
        emailText.setVisibility(View.INVISIBLE);
        passText.setVisibility(View.INVISIBLE);
        userText.setVisibility(View.INVISIBLE);
        passConfText.setVisibility(View.INVISIBLE);
        toLoginButton.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
    }

    /**
     * A simple function used to show the user interface after executing a background task.
     */
    private void showUI()
    {
        emailText.setVisibility(View.VISIBLE);
        passText.setVisibility(View.VISIBLE);
        userText.setVisibility(View.VISIBLE);
        passConfText.setVisibility(View.VISIBLE);
        toLoginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);

        bar.setVisibility(View.GONE);
    }

    private void goToAccount()
    {
    }
}