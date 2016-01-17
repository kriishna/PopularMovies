package com.jimandreas.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.jimandreas.popularmovies.FetchMovieListTask;
import com.jimandreas.popularmovies.TrafficManager;

import java.util.Iterator;

/**
 * if connected to wifi, walk through the popular and top_rated databases and
 * load the details
 */

public class MovieLoadDetails extends AsyncTask<String, Void, Void> {

    private final Context mContext;
    private TrafficManager mTM;
    private Iterator mIterator;

    public MovieLoadDetails(Context context) {
        mContext = context;
        mTM = TrafficManager.getInstance(mContext);
    }



    @Override
    protected Void doInBackground(String... params) {
        /*
         * walk the databases
         */
        /*
         * query for all tables that do not yet have details
         *   then walk the list and queue up another task to get the details!
         */
        Uri uri = MovieContract.MoviePopular.buildPopularMoviesUri();
        Cursor cursor_needs_details = mContext.getContentResolver().query(
                uri,
                new String[]{
                        MovieContract.MoviePopular.COLUMN_MOVIE_ID
                },
                MovieContract.MoviePopular.COLUMN_DETAILS_LOADED + " = 1",
                null,
                null,
                null);

        if (cursor_needs_details == null || !cursor_needs_details.moveToFirst()) {
            cursor_needs_details.close();
            return null;
        }

        /*
         * build list of movie_ids that need fetching of details
         */

        int movie_id;
        do {
            movie_id = cursor_needs_details.getInt(
                    cursor_needs_details.getColumnIndex(MovieContract.MoviePopular.COLUMN_MOVIE_ID));
            mTM.addPopularMovieToUpdate(movie_id);
        } while (cursor_needs_details.moveToNext());
        cursor_needs_details.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mTM.iteratePopularMoviesList();
    }
}


