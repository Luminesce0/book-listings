package com.omegaspockatari.booklistings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ${Michael} on 8/17/2016.
 */
class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_picture);
        if (currentBook.getmPicture() == null) {
            bookImage.setImageDrawable(null);
        } else {
            bookImage.setImageDrawable(LoadImageFromWebOperations(currentBook.getmPicture()));
        }

        TextView bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitle.setText(currentBook.getmTitle());

        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.book_author);
        bookAuthor.setText(currentBook.getmAuthor());

        TextView bookPublisher = (TextView) listItemView.findViewById(R.id.book_publisher);
        bookPublisher.setText(currentBook.getmPublisher());

        TextView bookCategory = (TextView) listItemView.findViewById(R.id.book_category);
        bookCategory.setText(currentBook.getmCategory());

        RatingBar bookRating = (RatingBar) listItemView.findViewById(R.id.book_rating);
        bookRating.setNumStars(currentBook.getmRating());



        return listItemView;
    }
    // http://stackoverflow.com/questions/6407324/how-to-get-image-from-url-in-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "Book Photo");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
