package com.lakehead.textbookmarket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Book_Info extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book__info);
        Intent intent = getIntent();
        Book myBook = intent.getParcelableExtra("books");
        ((ImageView)findViewById(R.id.image)).setImageBitmap(myBook.getBitmap());
        ((TextView) findViewById(R.id.BookTitle)).setText("Title: " + myBook.get_title());
        ((TextView)findViewById(R.id.Author)).setText("Author: " + myBook.get_author());
        ((TextView)findViewById(R.id.ISBN)).setText("ISBN: " + myBook.get_isbn());
        ((TextView)findViewById(R.id.edition)).setText("Edition: " + Integer.toString(myBook.get_edition()));
        ((TextView)findViewById(R.id.coverType)).setText("Cover Style: " + myBook.get_cover());
        ((TextView)findViewById(R.id.Publisher)).setText("Publisher: " + myBook.get_publisher());



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book__info, menu);
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
