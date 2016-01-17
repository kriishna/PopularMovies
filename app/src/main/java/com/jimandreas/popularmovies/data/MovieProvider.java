/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jimandreas.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * This class supplies Movie information from an encapsulated database.   It works in conjunction
 * with FetchMovieTask to provide data to the display fragments and the adapters that
 * fill out the UX.
 *
 * For more info, see
 * http://developer.android.com/guide/topics/providers/content-providers.html
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieDbHelper mOpenHelper;

    static final int MOVIE_POPULAR_LIST = 100;
    static final int MOVIE_TOP_RATED_LIST = 101;
    static final int MOVIE_FAVORITES_LIST = 102;

    static final int MOVIE_POPULAR_DETAIL = 103;
    static final int MOVIE_TOP_RATED_DETAIL = 104;
    static final int MOVIE_FAVORITE_DETAIL = 105;

    private static final SQLiteQueryBuilder sMoviePopularListQueryBuilder;
    private static final SQLiteQueryBuilder sMovieTopRatedListQueryBuilder;
    private static final SQLiteQueryBuilder sMovieFavoritesListQueryBuilder;

    static {
        sMoviePopularListQueryBuilder = new SQLiteQueryBuilder();
        sMoviePopularListQueryBuilder.setTables(
                MovieContract.MoviePopular.TABLE_NAME);
    }

    static {
        sMovieTopRatedListQueryBuilder = new SQLiteQueryBuilder();
        sMovieTopRatedListQueryBuilder.setTables(
                MovieContract.MovieTopRated.TABLE_NAME);
    }

    static {
        sMovieFavoritesListQueryBuilder = new SQLiteQueryBuilder();
        sMovieFavoritesListQueryBuilder.setTables(
                MovieContract.MovieFavorites.TABLE_NAME);
    }

    private static final String sMoviePopularIdSelection =
            MovieContract.MoviePopular.TABLE_NAME +
                    "." + MovieContract.MoviePopular.COLUMN_MOVIE_ID + " = ? ";

    private static final String sMovieTopRatedIdSelection =
            MovieContract.MovieTopRated.TABLE_NAME +
                    "." + MovieContract.MovieTopRated.COLUMN_MOVIE_ID + " = ? ";

    private static final String sMovieFavoritesIdSelection =
            MovieContract.MovieFavorites.TABLE_NAME +
                    "." + MovieContract.MovieFavorites.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMoviesByPopularity(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs = null;
        String selection = null;

        return sMoviePopularListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesByTopRating(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs = null;
        String selection = null;

        return sMovieTopRatedListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesByFavorites(Uri uri, String[] projection, String sortOrder) {

        String[] selectionArgs = null;
        String selection = null;

        return sMovieFavoritesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPopularMovieByID(Uri uri, String[] projection, String sortOrder) {

        String movie_id = MovieContract.MoviePopular.getMovieIDFromUri(uri);

        return sMoviePopularListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePopularIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTopRatedMovieByID(Uri uri, String[] projection, String sortOrder) {

        String movie_id = MovieContract.MovieTopRated.getMovieIDFromUri(uri);

        return sMovieTopRatedListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieTopRatedIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoritesMovieByID(Uri uri, String[] projection, String sortOrder) {

        String movie_id = MovieContract.MovieFavorites.getMovieIDFromUri(uri);

        return sMovieFavoritesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieFavoritesIdSelection,
                new String[] { movie_id },
                null,
                null,
                sortOrder
        );
    }

    /*
        This essential element in the ContentProvider architecture constructs
        a regular expression framework to parse the incoming URIs
     */
    static UriMatcher buildUriMatcher() {

        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(authority, MovieContract.PATH_POPULAR, MOVIE_POPULAR_LIST);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, MOVIE_TOP_RATED_LIST);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, MOVIE_FAVORITES_LIST);
        matcher.addURI(authority,
                MovieContract.PATH_POPULAR + "/" +
                MovieContract.PATH_MOVIE + "/#", MOVIE_POPULAR_DETAIL);
        matcher.addURI(authority,
                MovieContract.PATH_TOP_RATED + "/" +
                MovieContract.PATH_MOVIE + "/#", MOVIE_TOP_RATED_DETAIL);
        matcher.addURI(authority,
                MovieContract.PATH_FAVORITES + "/" +
                MovieContract.PATH_MOVIE + "/#", MOVIE_FAVORITE_DETAIL);

        // 3) Return the new matcher!

        return matcher;
    }


    // create the database if necessary

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case MOVIE_POPULAR_LIST:
                return MovieContract.MoviePopular.CONTENT_POPULAR_TYPE;
            case MOVIE_TOP_RATED_LIST:
                return MovieContract.MovieTopRated.CONTENT_TOP_RATED_TYPE;
            case MOVIE_FAVORITES_LIST:
                return MovieContract.MovieFavorites.CONTENT_FAVORITES_TYPE;
            case MOVIE_POPULAR_DETAIL:
                return MovieContract.MoviePopular.CONTENT_POPULAR_ITEM_TYPE;
            case MOVIE_TOP_RATED_DETAIL:
                return MovieContract.MovieTopRated.CONTENT_TOP_RATED_ITEM_TYPE;
            case MOVIE_FAVORITE_DETAIL:
                return MovieContract.MovieFavorites.CONTENT_FAVORITES_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_POPULAR_LIST:
            {
                retCursor = getMoviesByPopularity(uri, projection, sortOrder);
                break;
            }
            case MOVIE_POPULAR_DETAIL:
            {
                retCursor = getPopularMovieByID(uri, projection, sortOrder);
                break;
            }
            case MOVIE_TOP_RATED_LIST:
            {
                retCursor = getMoviesByTopRating(uri, projection, sortOrder);
                break;
            }
            case MOVIE_TOP_RATED_DETAIL:
            {
                retCursor = getTopRatedMovieByID(uri, projection, sortOrder);
                break;
            }
            case MOVIE_FAVORITES_LIST:
            {
                retCursor = getMoviesByFavorites(uri, projection, sortOrder);
                break;
            }
            case MOVIE_FAVORITE_DETAIL:
            {
                retCursor = getFavoritesMovieByID(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*

     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_POPULAR_LIST: {

                long _id = db.insert(MovieContract.MoviePopular.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MoviePopular.buildPopularMoviesUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_TOP_RATED_LIST: {

                long _id = db.insert(MovieContract.MovieTopRated.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieTopRated.buildTopRatedMoviesUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_FAVORITES_LIST: {

                long _id = db.insert(MovieContract.MovieFavorites.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieFavorites.buildFavoriteMoviesUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int numDeleted;

        switch (match) {

            case MOVIE_POPULAR_LIST: {
                numDeleted =  db.delete(MovieContract.MoviePopular.TABLE_NAME,
                        selection,
                        selectionArgs
                        );
                break;
            }
            case MOVIE_TOP_RATED_LIST: {
                numDeleted =  db.delete(MovieContract.MovieTopRated.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_FAVORITES_LIST: {
                numDeleted =  db.delete(MovieContract.MovieFavorites.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int numUpdated;

        switch (match) {

            case MOVIE_POPULAR_LIST: {
                numUpdated =  db.update(MovieContract.MoviePopular.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_POPULAR_DETAIL: {
                numUpdated =  db.update(MovieContract.MoviePopular.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_TOP_RATED_LIST: {
                numUpdated =  db.update(MovieContract.MovieTopRated.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_TOP_RATED_DETAIL: {
                numUpdated =  db.update(MovieContract.MovieTopRated.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_FAVORITES_LIST: {
                numUpdated =  db.update(MovieContract.MovieFavorites.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }
            case MOVIE_FAVORITE_DETAIL: {
                numUpdated =  db.update(MovieContract.MovieFavorites.TABLE_NAME,
                        values, selection,
                        selectionArgs
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numUpdated;
    }

    // movie favorites won't ever get downloaded from TheMovieDb - so
    // punt on the bulkInsert method...
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE_POPULAR_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MoviePopular.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case MOVIE_TOP_RATED_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieTopRated.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}