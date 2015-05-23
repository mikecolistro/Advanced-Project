package com.lakehead.textbookmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Course_Info extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course__info);

        Intent intent = getIntent();
        Course thisCourse = intent.getParcelableExtra("course");

        ((TextView)findViewById(R.id.Title)).setText("Title: " + thisCourse.get_title());
        ((TextView)findViewById(R.id.CCode)).setText("Course Code: " + thisCourse.get_code());
        ((TextView)findViewById(R.id.Professor)).setText("Instructor: " + thisCourse.get_instructor());
        ((TextView)findViewById(R.id.courseSection)).setText("Section: " + thisCourse.get_section());
        ((TextView)findViewById(R.id.courseTerm)).setText("Term: " + thisCourse.get_term());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course__info, menu);
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
