package com.omegaspockatari.booklistings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String API_KEY = "&key=AIzaSyC9OlGW-KQD_9KJbCGKxVJjn6g11Vohqno";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String searchTerm;
    private EditText userInput;
    private Button searchButton;
    private ArrayList<Book> tempBookArrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.search_books_button);
        userInput = (EditText) findViewById(R.id.edit_text);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTerm = userInput.getText().toString();
                if (searchTerm.trim().length() <= 0 || searchTerm.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "No Search Entered", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Error Response code: No Search Given");
                } else {
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute();
                }
            }
        });

        if (tempBookArrayList != null) {
            ListView bookListView = (ListView) findViewById(R.id.list);

            BookAdapter adapter = new BookAdapter(this, tempBookArrayList);

            bookListView.setAdapter(adapter);
        }

    }


    private void updateUi(ArrayList<Book> books) {

        tempBookArrayList = books;

        if (books != null) {
            ListView bookListView = (ListView) findViewById(R.id.list);

            BookAdapter adapter = new BookAdapter(this, books);

            bookListView.setAdapter(adapter);
        } else {
            Log.e(LOG_TAG, "Still suffering from random void errors and no results with a correct string");
        }
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {

            URL url = null;
            /**
             * TODO Figure out why this stuff is acting strange
             */

            url = createUrl(searchTerm.trim());

            // Perform an HTTP request to the URL --> Receive JSON response back.
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            ArrayList<Book> books = extractBookFromJson(jsonResponse);

            searchTerm = "";

            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }

            updateUi(books);
        }


        private URL createUrl(String searchTerm) {
            /**
             * TODO Possibly change this into just books/v1/ and allow for the variation of
             * Author/title/publisher searches? Might be complex. We'll see! We can have a search
             * selection method sort of possibility perhaps??? Dunno...
             */
            String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=";
            String completeUrl = baseUrl + searchTerm.replace(" ", "20%") + API_KEY;
            URL url = null;
            try {
                url = new URL(completeUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error Creating URL", e);
            }

            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    Log.e(LOG_TAG, "Response Code: " + urlConnection.getResponseCode());
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error Response Code: " + urlConnection.getResponseCode()
                            + " " + url.toString());
                }

            } catch (IOException e) {
                if (urlConnection.getResponseCode() != 200) {
                    Log.e(LOG_TAG, "Error Retrieving Earthquake JSON results", e);
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    // Append the first line read by reader and put into StringBuilder output.
                    output.append(line);
                    // Get the next line read by reader and put into the string.
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        private ArrayList<Book> extractBookFromJson(String bookJSON) {

            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }

            ArrayList<Book> books = new ArrayList<>();


            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray bookArray = baseJsonResponse.getJSONArray("items");
                int length = bookArray.length();
                for (int i = 0; i < length; i++) {

                    /**
                     * Temporary variables for storing/augmenting data to push to a book object
                     */

                    int jAuthor = 0;
                    int jCategory = 0;
                    StringBuilder tempBuilder = new StringBuilder();

                    JSONObject bookObject = bookArray.getJSONObject(i);
                    JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");
                    JSONObject bookPictures = bookObject.getJSONObject("imageLinks");

                    String title = bookInfo.getString("title");
                    String publisher = bookInfo.getString("publisher");
                    int rating = bookInfo.getInt("averageRating");
                    String picture = bookPictures.getString("thumbnail");

                    JSONArray authors = bookInfo.getJSONArray("authors");

                    /**
                     * Loop functions for the author(s) array
                     */
                    while (jAuthor < authors.length()) {
                        tempBuilder.append(authors.get(i));
                        if (authors.length() > 1) {
                            tempBuilder.append(" ");
                        }
                        jAuthor++;
                    }

                    String author = tempBuilder.toString();

                    /**
                     * Loop functions for the category(ies) array
                     */
                    JSONArray categories = bookInfo.getJSONArray("categories");
                    tempBuilder.delete(0, tempBuilder.length());


                    while (jCategory < categories.length()) {
                        tempBuilder.append(categories.get(i));
                        if (categories.length() > 1) {
                            tempBuilder.append(" ");
                        }
                        jCategory++;
                    }

                    String category = tempBuilder.toString();

                    Log.v(LOG_TAG, title + " " + author + " " + publisher + " " + rating + " " +
                            category + " " + "picture");
                    books.add(new Book(title, author, publisher, rating, category, picture));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return books;
        }

    }

}
