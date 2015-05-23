package com.lakehead.textbookmarket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONArray;
import org.apache.http.client.HttpClient;
import android.util.Log;
import android.os.AsyncTask;

/**
 * The first thing you'll notice is the stuff between the angle brackets. These are:
 * <Param type for doInBackground(),
 * Param type for onProgressUpdate() (We don't use this),
 * Return type of doInBackground() and argument type for onPostExecute()>
 * <br/>
 * When a new AsyncTask is executed, doInBackground() gets called with any params provided
 * (the params array is variable length). After a value is returned from doInBackground(),
 * onPostExecute() is called, passing in whatever value was returned. Note that the type of
 * argument in onPostExecute() must match the return type of doInBackground(); in this case,
 * it's a JSONArray.
 * <br/>
 * The OnTaskCompleted is on Object that implements a method called onTaskCompleted(). This
 * interface is used to facilitate a simple observer pattern. This allows us to pass objects
 * back into whatever created this AsyncTask. In this case, we pass the JSONArray we get
 * from the API call back into another object (most likely an Activity or Fragment).
 * <br/>
 * A NameValuePair is essentially just a group of two strings - one is a key, the other, a value.
 */
class GetJSONArrayTask extends AsyncTask<NameValuePair, Void, JSONArray> {

    //listener is the object that created this AsyncTask. It implements a callback function.
    private OnTaskCompleted listener;

    //path refers to the URL, excluding the address and the port. "/api/book", for example.
    String path;
    JSONArray jArray;
    private static final String SERVER_ADDRESS = "http://107.170.7.58:4567";

    /**
     * Constructor
     * @param listener
     */
    public GetJSONArrayTask(OnTaskCompleted listener)
    {
        this.listener=listener;
    }

    /**
     * Constructor
     * @param listener
     * @param path
     */
    public GetJSONArrayTask(OnTaskCompleted listener, String path)
    {
        this.listener = listener;
        this.path = path;
    }

    /**
     * This method actually executes the AsyncTask.
     * POST parameters are passed in as NameValuePairs.
     * @param params
     * @return A JSONArray as a response from the server.
     */
    protected JSONArray doInBackground(NameValuePair... params)
    {
        String result=new String();

        try
        {
            /*
            Note that the URI we use consists of a final String concatenated with the path that
            is passed in through the constructor
            */
            URI uri = new URI(SERVER_ADDRESS + path);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);

            //The POST parameters are will be added as NameValuePairs
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            //Add every NameValuePair to a list
            for(NameValuePair p: params)
            {
                nameValuePairs.add(p);
            }

            //Add all our POST parameters to the request
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute the request and get a response
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(entity != null)
            {
                //Try to read the result from the HTTP request into a String.
                //Inside this try/catch, we just build a string from the result of our API call
                try
                {
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                }
                catch (Exception e) //Couldn't grab the string from HttpEntity for some reason
                {
                    Log.e("Exceptions", "Error converting result -> " + e.toString());
                }

                // try create a JSONArray from the string we just got
                try
                {
                    jArray = new JSONArray(result);
                    return jArray;

                }
                catch (JSONException e) //malformed JSON, not JSON, possibly a null/empty String, etc
                {
                    Log.d("Debug","Error parsing JSON");
                    Log.e("Exceptions", e.toString());
                }
            }
            else //The HttpEntity was null
            {
                return null;
            }
        }
        catch(Exception e) //URL can't be reached, bad params, etc
        {
            Log.d("Debug","Getting URL failed!");
            Log.e("Exceptions",e.toString());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    //Callback for the activity/fragment.
    //Pass the JSONArray back to whatever created this AsyncTask.

    /**
     * This method executes a callback to the listener that originally created this AsyncTask.
     * The callback provides the listener with the JSONArray that was received from the POST request.
     * @param jArray
     */
    protected void onPostExecute(JSONArray jArray)
    {
        Log.i("GetJsonArrayTask", "onPostExecute() Received JSON -> " + jArray.toString());
        listener.onTaskCompleted(jArray);
    }
}