package com.jimandreas.popularmovies.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.jimandreas.popularmovies.TrafficManager;

import com.jimandreas.popularmovies.data.MovieContract.MovieFavorites;

/**
 *
 */
public class MovieLoadFavorites extends AsyncTask<String, Void, Void> {

    private final Context mContext;
    private TrafficManager mTM;

    public MovieLoadFavorites(Context context) {
        mContext = context;
        mTM = TrafficManager.getInstance(mContext);
    }

    @Override
    protected Void doInBackground(String... params) {
        /*
         * pull the favorites table from the database.
         * the MovieDetailFragment adds the favorite row to the table,
         * but never removes it.   It just toggles the favorite entry in
         * the row as the user toggles the Heart on the UX.
         * Prune out any Unfavorited rows now.
         */
        Uri uri = MovieContract.MovieFavorites.buildFavoriteMoviesUri();
        Cursor fav_cursor = mContext.getContentResolver().query(
                uri,
                new String[]{
                        MovieFavorites.COLUMN_MOVIE_ID,
                        MovieFavorites.COLUMN_FAVORITE
                },
                null,
                null,
                null,
                null);

        if (fav_cursor == null || !fav_cursor.moveToFirst()) {
            return null;
        }
        Integer favorite_id;
        Integer isFavorite;
        do {
            favorite_id = fav_cursor.getInt(fav_cursor.getColumnIndex(
                    MovieFavorites.COLUMN_MOVIE_ID));
            isFavorite = fav_cursor.getInt(fav_cursor.getColumnIndex(
                    MovieFavorites.COLUMN_FAVORITE));
            if (isFavorite == 1) {
                mTM.addFavoriteID(favorite_id);
            } else {
                int num_deleted = mContext.getContentResolver().delete(
                        uri,
                        MovieFavorites.COLUMN_MOVIE_ID + " = " + favorite_id,
                        null
                        );
            }
        } while (fav_cursor.moveToNext());
        return null;
    }

}
