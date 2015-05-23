package com.lakehead.textbookmarket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment used in the "My Listings" tab of MainActivity
 */
public class MyListingsFragment extends Fragment implements OnTaskCompleted{
    ListView listingsListView;
    View rootView;
    JSONArray jArray;
    ArrayList<Listing> listingsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_listings, container, false);
        listingsListView = (ListView)rootView.findViewById(R.id.listings_list_view);

        if(savedInstanceState != null)
        {
            Log.d("MyListingsFragment", "onCreateView() -> " + "Found Saved Instance state. Loading Course list from it...");
            listingsList = savedInstanceState.getParcelableArrayList("listingsList");
            MyListingsArrayAdapter listingsAdapter = new MyListingsArrayAdapter(this.getActivity(), listingsList);
            listingsListView.setAdapter(listingsAdapter);
        }
        else
        {
            Log.d("MyListingsFragment", "onCreateView() -> " + "No Saved Instance state. Loading Course list from API...");
            NameValuePair ext = new BasicNameValuePair("ext", "json");
            NameValuePair count = new BasicNameValuePair("count", "20");
            new GetJSONArrayTask(this, "/api/sell").execute(ext, count);
        }

        return rootView;
    }


    @Override
    public void onPause()
    {
        Log.d("MyListingsFragment", "onPause() -> " + "paused fragment.");
        super.onPause();
    }

    @Override
    public void onResume()
    {
        Log.d("MyListingsFragment", "onResume() -> " + "resumed fragment.");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d("MyListingsFragment", "onSaveInstanceState() -> " + "state saved for fragment.");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listingsList", listingsList);


    }

    @Override
    public void onTaskCompleted(Object obj) {
        listingsList= new ArrayList<Listing>();
        List<Book> temporaryBookList = new ArrayList<Book>();

        jArray = (JSONArray)obj;
        Log.i("MyListingsFragment","jArray length -> " + jArray.length());

        String nodeType;
        JSONObject nodeData;

        try{
            for(int i = 0 ; i < jArray.length(); i++ ){

                nodeType = jArray.getJSONObject(i).getString("kind");
                nodeData = jArray.getJSONObject(i).getJSONObject("data");
                Log.v("MyListingsFragment", "OnTaskCompleted() -> Kind is: " + nodeType);
                if(nodeType.equals("sell"))
                {
                    Log.v("MyListingsFragment", "OnTaskCompleted() -> Sell Data Polled -> " + nodeData.toString());
                    listingsList.add(generateListingFromJSONNode(nodeData));
                }
                else if(nodeType.equals("book"))
                {
                    Log.v("MyListingsFragment", "OnTaskCompleted() -> Book Data Polled -> " + nodeData.toString());
                    temporaryBookList.add(Book.generateBookFromJSONNode(nodeData));
                }
                else
                {
                    Log.e("MyListingsFragment", "OnTaskCompleted() -> NODE IS NEITHER BOOK NOR SELL!!!! Node Data is: " + nodeData.toString());
                }
            }

            for(Listing current_listing : listingsList){
                for(Book current_book : temporaryBookList){
                    if(current_listing.get_book_id() == current_book.get_id()){
                        Log.v("MyListingsFragment", "OnTaskCompleted() -> Associated Book with ID {" + current_book.get_id()
                                + "} with Listing {" + current_listing.get_id()+"} which was requesting Book with ID {"
                                + current_listing.get_book_id()+"}");
                        current_listing.set_book(current_book);

                    }
                }
                if(current_listing.get_book() == null){
                    Log.e("MyListingsFragment","OnTaskCompleted() -> Could not associate a book to Listing with ID {"
                            + current_listing.get_id()+"} as it was requesting Book ID {" + current_listing.get_book_id()
                            +"} which does not exist in our Temporary Book List. CONTACT API TEAM!!!!");
                }
            }

        }catch(Exception e){
            Log.e("MyListingsFragment", "OnTaskCompleted() -> HighLevel Catch -> "+ e.toString());
            e.printStackTrace();
        }

        MyListingsArrayAdapter listingsAdapter = new MyListingsArrayAdapter(this.getActivity(), listingsList);
        listingsListView.setAdapter(listingsAdapter);

    }

    /**
     * Generates a Listing Object based on the JSON Node passed to it.
     *
     * @param listingDataNode the associated listing JSON Node pulled from the API.
     * @return Returns a listing based on the JSON Node information. Returns null if it failed to parse
     * important data.
     */
    public Listing generateListingFromJSONNode(JSONObject listingDataNode){

        //temporary listing variables.
        int listing_id;
        int user_id;
        int edition_id;
        double price;
        String start_date;
        String end_date;

        try{
            listing_id =  listingDataNode.getInt("id");
        }catch(Exception e){
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for listing_id!!! Failing: " + e.toString());
            return null;
        }
        try{
            user_id = listingDataNode.getInt("user_id");
        }catch(Exception e){
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for user_id!!! Failing: " + e.toString());
            return null;
        }
        try{
            edition_id =  listingDataNode.getInt("edition_id");
        }catch(Exception e){
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for edition_id!!! Failing: " + e.toString());
            return null;
        }
        try{
            price = listingDataNode.getDouble("price");
        }catch(Exception e){
            price = 9999.99;
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for price: " + e.toString());
        }
        try{
            start_date = listingDataNode.getString("start_date");
        }catch(Exception e){
            start_date = "N/A";
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for start_date: " + e.toString());
        }
        try{
            end_date = listingDataNode.getString("end_date");
        }catch(Exception e){
            end_date = "N/A";
            Log.e("MyListingsFragment", "generateListingFromJSONNode() -> Couldn't parse JSON for end_date: " + e.toString());
        }

        return new Listing(listing_id,user_id,edition_id,price,start_date,end_date);

    }

}