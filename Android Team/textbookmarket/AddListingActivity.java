package com.lakehead.textbookmarket;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AddListingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        Bundle bundle = getIntent().getExtras();
        Book selected_book = bundle.getParcelable("book");
        Log.i("AddListingActivity", "OnCreate() -> Received Parcelable book with title: " + selected_book.get_title());

        TextView titleTextView = (TextView)findViewById(R.id.textViewBookTitle);
        TextView authorTextView = (TextView)findViewById(R.id.textViewAuthor);
        TextView editionTextView = (TextView)findViewById(R.id.textViewEditionNumber);
        TextView msrpTextView = (TextView)findViewById(R.id.textViewMSRP);
        TextView publisherTextView = (TextView)findViewById(R.id.textViewPublisher);
        ImageView iconImageView = (ImageView)findViewById(R.id.bookImageIcon);

        titleTextView.setText(selected_book.get_title());
        authorTextView.setText(selected_book.get_author());
        editionTextView.setText("Edition: "+ String.valueOf(selected_book.get_edition()));
        publisherTextView.setText(selected_book.get_publisher());

        if(selected_book.getBitmap() == null)
        {
            new GetImageTask(selected_book.get_image_url(), iconImageView, 190, 190,selected_book).execute();
        }
        else
        {
            iconImageView.setImageBitmap(selected_book.getBitmap());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
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


}
