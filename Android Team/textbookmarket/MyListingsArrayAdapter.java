package com.lakehead.textbookmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Master on 2/17/14.
 */
public class MyListingsArrayAdapter extends ArrayAdapter<Listing> {
    private List<Listing> listingList;
    private Context context;
    private final int THUMBNAIL_SIZE = 96;

    public MyListingsArrayAdapter(Context context, List<Listing> listings){
        super(context, R.layout.mylistings_item_view,  listings);
        this.listingList = listings;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.mylistings_item_view, parent, false);

        //grabbing XML elements
        TextView listingTitleView = (TextView)rowView.findViewById(R.id.listingTitle);
        TextView listingCourseView = (TextView)rowView.findViewById(R.id.listingCourseInfo);
        TextView listingPriceView = (TextView)rowView.findViewById(R.id.listingPrice);
        ImageView bookIconImageView = (ImageView)rowView.findViewById(R.id.listing_icon);

        //getting the listing's book for future usage.
        Book relatedBook = listingList.get(position).get_book();

        //populating XML elements
        listingTitleView.setText(relatedBook.get_title());
        listingPriceView.setText("Price: $" + listingList.get(position).get_price());
        listingCourseView.setText(listingList.get(position).get_start_date());

        //If the book doesn't have a bitmap yet, then fetch it. Otherwise, just display the one we have
        if(listingList.get(position).get_book().getBitmap() == null)
        {
            new GetImageTask(relatedBook.get_image_url(), bookIconImageView, THUMBNAIL_SIZE, THUMBNAIL_SIZE,relatedBook).execute();
        }
        else
        {
            bookIconImageView.setImageBitmap(relatedBook.getBitmap());
        }

        return rowView;

    }

}
