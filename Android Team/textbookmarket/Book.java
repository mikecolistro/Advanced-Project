package com.lakehead.textbookmarket;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Master on 2/10/14.
 */
public class Book implements Parcelable{
    private final int _id;
    private final String _title;
    private final String _isbn;
    private final int _edition_group_id;
    private final String _author;
    private final int _edition;
    private final String _publisher;
    private final String _cover;
    private final String _image_url;//a URL pointing to the image.
    private Bitmap bitmap;
    

    /**
     * @param id        Internally assigned ID.
     * @param title     Title of the book.
     * @param isbn      The ISBN used to distinguish from other editions of same book.
     * @param book_id   Internally assigned Book ID.
     * @param author    Author of the book.
     * @param edition   Edition number expressed as an integer.
     * @param publisher The publishing company
     * @param cover     Paperback/Hardcover
     * @param image_url The URL pointing to the image hosted by LakeheadU's Bookstore
     */
    public Book(int id, String title, String isbn, int book_id, String author, int edition, String publisher, String cover, String image_url) {
        _id = id;
        _title = title;
        _isbn = isbn;
        _edition_group_id = book_id;
        _author = author;
        _edition = edition;
        _publisher = publisher;
        _cover = cover;
        _image_url = image_url;
    }

    /**
     * This constructor must only be used to create sentinel books used to tell the BookArrayAdapter
     * to insert a "loading" row.
     */
    public Book() {
        _id = -1;
        _title = null;
        _isbn = null;
        _edition_group_id = -1;
        _author = null;
        _edition = -1;
        _publisher = null;
        _cover = null;
        _image_url = null;
    }

    // Parcelling part
    public Book(Parcel in){
        _id = in.readInt();
        _title = in.readString();
        _isbn = in.readString();
        _edition_group_id = in.readInt();
        _author = in.readString();
        _edition = in.readInt();
        _publisher = in.readString();
        _cover = in.readString();
        _image_url = in.readString();
        bitmap = (Bitmap)in.readValue(ClassLoader.getSystemClassLoader());
    }

    public String get_image_url() {
        return _image_url;
    }

    public int get_id() {
        return _id;
    }

    public String get_isbn() {
        return _isbn;
    }

    public int get_edition_group_id() {
        return _edition_group_id;
    }

    public String get_author() {
        return _author;
    }

    public int get_edition() {
        return _edition;
    }

    public String get_publisher() {
        return _publisher;
    }

    public String get_cover() {
        return _cover;
    }

    public String get_title() {
        return _title;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_title);
        dest.writeString(_isbn);
        dest.writeInt(_edition_group_id);
        dest.writeString(_author);
        dest.writeInt(_edition);
        dest.writeString(_publisher);
        dest.writeString(_cover);
        dest.writeString(_image_url);
        dest.writeValue(bitmap);

    }

    /**
     * A class necessary for parcelable to work. Handles Creating Parceled Versions of books
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    /**
     * A small static book generator to reduce code duplication.
     * @param bookDataNode A JSON Node that holds Data of the "book" kind.
     * @return A Book object corresponding to the data, or null.
     */
    public static Book generateBookFromJSONNode(JSONObject bookDataNode){

        int edition;
        int book_id;
        String title;
        String isbn;
        int edition_group_id;
        String author;
        String publisher;
        String cover;
        String image;

        Log.i("Book", "Book Data Polled -> " + bookDataNode.toString());
        try{
            book_id = bookDataNode.getInt("id");
        }catch(Exception e){
            book_id = 0;
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for id: " + e.toString());
        }
        try{
            title = bookDataNode.getString("title");
        }catch(Exception e){
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for title: " + e.toString());
            return null;
        }
        try{
            isbn = bookDataNode.getString("isbn");
        }catch(Exception e){
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for ISBN: " + e.toString());
            return null;
        }
        try{
            edition_group_id = bookDataNode.getInt("edition_group_id");
        }catch(Exception e){
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for edition_group_id: " + e.toString());
            return null;
        }try{
            author = bookDataNode.getString("author");
        }catch(Exception e){
            author = "N/A";
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for author: " + e.toString());

        }
        try{
            edition = bookDataNode.getInt("edition");
        }catch(Exception e){
            edition = 0;
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for edition: " + e.toString());
        }
        try{
            publisher = bookDataNode.getString("publisher");
        }catch(Exception e){
            publisher = "N/A";
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for publisher: " + e.toString());
        }
        try{
            cover = bookDataNode.getString("cover");
        }catch(Exception e){
            cover = "N/A";
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for cover: " + e.toString());
        }
        try{
            image = bookDataNode.getString("image");
        }catch(Exception e){
            image = "N/A";
            Log.e("Book", "generateBookFromJSONNode() -> Couldn't parse JSON for image: " + e.toString());
        }


        return new Book(book_id, title, isbn, edition,
                author, edition, publisher, cover, image);
    }
}
