package com.omegaspockatari.booklistings;

/**
 * Created by ${Michael} on 8/17/2016.
 */
public class Book {

    private  String mTitle;

    private  String mAuthor;

    private  String mPublisher;

    private  int mRating;

    private  String mCategory;

    private String mPicture;

    public Book(String mTitle, String mAuthor, String mPublisher, int mRating, String mCategory, String picture) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
        this.mPublisher = mPublisher;
        this.mRating = mRating;
        this.mCategory = mCategory;
        mPicture = picture;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPublisher() {
        return mPublisher;
    }

    public int getmRating() {
        return mRating;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmPicture() {
        return mPicture;
    }
}
