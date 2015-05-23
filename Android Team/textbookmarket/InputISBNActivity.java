package com.lakehead.textbookmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class that handles ISBN Lookup.
 */
public class InputISBNActivity extends Activity implements OnTaskCompleted{
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_isbn);
        bar = (ProgressBar)findViewById(R.id.isbnProgressBar);
        bar.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback for when the scan button is clicked. Starts the ISBN Scanner
     * @param view Generic parameter for onClick callback
     */
    public void scanClicked(View view){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan(IntentIntegrator.BOOK_CODE_TYPES);
    }

    /**
     * This is called when the scanner returns, either with data or without.
     * @param requestCode The request code originally sent to the scanner
     * @param resultCode The resulting code from the scanner
     * @param intent the intent returning a result.
     */
    public void onActivityResult(int requestCode, int resultCode,Intent intent){
        try{
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult != null){
            //had to do this stupid double-if statement because occasionally the scanner will return
            //null as the STRING and occasionally will return null as the scanningResult
            String scanContent = scanningResult.getContents();
            if(scanContent != null && isISBN13Valid(scanContent)){
                Log.d("InputISBNActivity","This is the ISBN " + scanContent);
                Log.d("InputISBNActivity", "This is the scanFormat " + scanningResult.getFormatName());
                TextView isbnTextView = (TextView)findViewById(R.id.isbnText);
                isbnTextView.setText(scanContent);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No ISBN data received. Is this a valid ISBN13?", Toast.LENGTH_SHORT);
                toast.show();
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
        }catch(Exception e){Log.e("InputISBNActivity","Error during reading of scan data: " + e.toString());}
    }

    /**
     * Callback for the search button being pressed. Currently only sets the progress bar spinning.
     * @param view Generic parameter for onClick callback
     */
    public void okClicked(View view){
        TextView isbnTextView = (TextView)findViewById(R.id.isbnText);
        isbnTextView.clearFocus();
        ProgressBar bar = (ProgressBar)findViewById(R.id.isbnProgressBar);
        bar.setVisibility(View.VISIBLE);
        Log.i("InputISBNActivity", "okClicked() - isbn text is: " + isbnTextView.getText());
        NameValuePair isbn = new BasicNameValuePair("isbn", String.valueOf(isbnTextView.getText()));
        new GetJSONArrayTask(this, "/api/book").execute(isbn);



    }

    /**
     * Check's whether a string is a valid ISBN13
     * @param isbn The isbn number to check for validity.
     * @return whether or not the scanned barcode is a valid ISBN13 number.
     */
    public boolean isISBN13Valid(String isbn) {
        int check = 0;
        for (int i = 0; i < 12; i += 2) {
            check += Integer.valueOf(isbn.substring(i, i + 1));
        }
        for (int i = 1; i < 12; i += 2) {
            check += Integer.valueOf(isbn.substring(i, i + 1)) * 3;
        }
        check += Integer.valueOf(isbn.substring(12));
        return check % 10 == 0;
    }

    /**
     * This determines whether the JSONArray received from the API is a relevant book, thus telling
     * us whether or not the ISBN is legitimate to us. If so, it starts a new intent with the book.
     * Otherwise, it displays a toast indicating an error message.
     * @param obj JSONArray of a single book.
     */
    @Override
    public void onTaskCompleted(Object obj) {
        JSONArray jArray = (JSONArray)obj;
        Book found_book = null;
        bar.setVisibility(View.INVISIBLE);

        if(!jArray.isNull(0)){
        try{
            JSONObject nodeData = jArray.getJSONObject(0).getJSONObject("data");
            found_book = Book.generateBookFromJSONNode(nodeData);
        }
        catch(Exception e)
        {
            Log.e("InputISBNActivity", "OnTaskCompleted() -> Couldn't parse a DATA object out of the array. Error:  " + e.toString());
            return;
        }
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No associated books found for that ISBN.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        Log.i("InputISBNActivity", "OnTaskCompleted() -> " + "Found legal JSONArray, starting newbook intent.");
        Intent addListingIntent = new Intent(this, AddListingActivity.class);
        addListingIntent.putExtra("book",found_book);
        startActivity(addListingIntent);



    }
}
