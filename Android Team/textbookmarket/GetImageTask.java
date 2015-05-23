package com.lakehead.textbookmarket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URI;

/**
 * This AsyncTask grabs a Bitmap from a URL and then puts it inside an ImageView that
 * is passed in though the constructor. Optionally we may maintain a reference to that
 * Bitmap inside a Book object if we choose to pass one in.
 * <br/>
 * AsyncTasks are more thoroughly explained in GetJSONArrayTask
 */

class GetImageTask extends AsyncTask<Void, Void, Bitmap> {

    //This ImageView is the one whose bitmap we want to set
    private ImageView imageView;
    private int width;
    private int height;
    private String url;
    private Book book;

    /**
     *
     * @param url The URL of the image
     * @param imageView The actual imageView holding the image
     * @param width Image Width
     * @param height Image Height
     */
    public GetImageTask(String url, ImageView imageView, int width, int height)
    {
        this.url = url;
        this.imageView=imageView;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor. When this constructor is used, the url of the image is also stored in book object.
     * @param url The URL of the image
     * @param imageView The actual imageView holding the image
     * @param width Image Width
     * @param height Image Height
     * @param book the object in which the image is stored.
     */
    public GetImageTask(String url, ImageView imageView, int width, int height, Book book)
    {
        this.url = url;
        this.imageView=imageView;
        this.width = width;
        this.height = height;
        this.book = book;
    }

    //Params are of type Void because we never need them for this AsyncTask
    /**
     * @return A bitmap to be applied to an ImageView
     * This method executes the request and fetches an image
     */
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap;
        try
        {
            URI uri = new URI(url);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet method = new HttpGet(uri);

            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if(entity != null)
            {
                if(response.getStatusLine().getStatusCode() == 200)
                {
                    BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
                    InputStream input = b_entity.getContent();

                    bitmap = BitmapFactory.decodeStream(input);
                    return bitmap;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            Log.e("Exceptions",e.toString());
            return null;
        }
    }

    //Here, we scale the image we got to a desired size, and set the ImageView's bitmap
    //Note that in this AsyncTask, we don't have a callback to an Activity or Fragment
    /**
     * The Bitmap parameter is received from the doInBackground method on return.
     * The Bitmap is applied to an ImageView specified in the constructor with dimensions width, height.
     * If a book object was passed into the constructor, we also store the URL of that image in a
     * book object.
     * @param bmp
     */
    protected void onPostExecute(Bitmap bmp)
    {
        if(bmp != null){
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, width, height, false);
            imageView.setImageBitmap(scaledBitmap);

            //We may or may not have passed a book into this AsyncTask
            if(book != null)
            {
                book.setBitmap(scaledBitmap);
            }
        }else{
            Log.e("GetImageTask", "onPostExecute() -> " + "Bitmap is null, cannot create BMP. It's possible the URL did not respond or return an image.");
        }

    }
}