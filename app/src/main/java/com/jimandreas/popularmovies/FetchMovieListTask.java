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
package com.jimandreas.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.jimandreas.popularmovies.data.MovieContract;
import com.jimandreas.popularmovies.data.MovieContract.MoviePopular;
import com.jimandreas.popularmovies.data.MovieContract.MovieTopRated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class FetchMovieListTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieListTask.class.getSimpleName();
    private final Boolean EXTRA_VERBOSE = true;

    private final Context mContext;
    private final TrafficManager mTM;

    public FetchMovieListTask(Context context) {
        mContext = context;
        mTM = TrafficManager.getInstance(mContext);
    }

    private boolean DEBUG = true;

    public final String FETCH_POPULAR = "popular";
    public final String FETCH_TOP_RATED = "toprated";
    public final String FETCH_POPULAR_MOVIE_DETAILS = "pop_details";
    public final String FETCH_TOP_RATED_MOVIE_DETAILS = "tr_details";

    /**
     * Take the String representing the complete movie info in JSON Format and
     * pull out the data we need to construct the Strings needed for the presentation.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieListFromJson(String movieListJsonStr, String whatToDo
                    ) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        //  COLUMNS:  id, overview, poster_path, release date, title, vote_average, vote_count

        final String MOVIEDB_RESULTS = "results";

        final String MOVIEDB_ID = "id";
        final String MOVIEDB_OVERVIEW = "overview";
        final String MOVIEDB_POSTER_PATH = "poster_path";
        final String MOVIEDB_RELEASE_DATE = "release_date";
        final String MOVIEDB_POPULARITY = "popularity";
        final String MOVIEDB_TITLE = "title";
        final String MOVIEDB_VOTE_AVERAGE = "vote_average";
        final String MOVIEDB_VOTE_COUNT = "vote_count";



        try {
            JSONObject movieListJson = new JSONObject(movieListJsonStr);
            JSONArray movieListArray = movieListJson.getJSONArray(MOVIEDB_RESULTS);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieListArray.length());
            Uri uri = null;

            for(int i = 0; i < movieListArray.length(); i++) {
                // These are the values that will be collected.

                int id;
                String overview;
                String poster_path;
                String release_date;
                String popularity;
                String title;
                String vote_average;
                String vote_count;

                JSONObject movieInfo = movieListArray.getJSONObject(i);

                id = movieInfo.getInt(MOVIEDB_ID);
                overview = movieInfo.getString(MOVIEDB_OVERVIEW);
                poster_path = movieInfo.getString(MOVIEDB_POSTER_PATH);
                release_date = movieInfo.getString(MOVIEDB_RELEASE_DATE);
                popularity = movieInfo.getString(MOVIEDB_POPULARITY);
                title = movieInfo.getString(MOVIEDB_TITLE);
                vote_average = movieInfo.getString(MOVIEDB_VOTE_AVERAGE);
                vote_count = movieInfo.getString(MOVIEDB_VOTE_COUNT);


                ContentValues movieValues = new ContentValues();

                if (whatToDo.contains(FETCH_TOP_RATED)) {
                    
                    uri = MovieTopRated.CONTENT_URI;
                    movieValues.put(MovieTopRated.COLUMN_MOVIE_ID, id);
                    movieValues.put(MovieTopRated.COLUMN_POPULAR_INDEX, popularity);

                    movieValues.put(MovieTopRated.COLUMN_OVERVIEW, overview);
                    movieValues.put(MovieTopRated.COLUMN_POSTER_PATH, poster_path);
                    movieValues.put(MovieTopRated.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MovieTopRated.COLUMN_TITLE, title);
                    movieValues.put(MovieTopRated.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(MovieTopRated.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MovieTopRated.COLUMN_MYNAME, "toprated");


                } else {
                    uri = MoviePopular.CONTENT_URI;
                    movieValues.put(MoviePopular.COLUMN_MOVIE_ID, id);
                    movieValues.put(MoviePopular.COLUMN_POPULAR_INDEX, popularity);

                    movieValues.put(MoviePopular.COLUMN_OVERVIEW, overview);
                    movieValues.put(MoviePopular.COLUMN_POSTER_PATH, poster_path);
                    movieValues.put(MoviePopular.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MoviePopular.COLUMN_TITLE, title);
                    movieValues.put(MoviePopular.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(MoviePopular.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MoviePopular.COLUMN_MYNAME, "popular");

                }

                cVVector.add(movieValues);
            }

            if (whatToDo.contains(FETCH_POPULAR)) {
                mTM.buildPopularMoviesListToUpdate();
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(uri, cvArray);
            }

            if (EXTRA_VERBOSE) Log.i(LOG_TAG, "download complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /*
     * Details Details!!   Get movie details - including trailers and reviews
     * (1st three of each only) from TheMovieDB...
     */
    private void getMovieDetailsFromJson(String movieInfoJsonStr, String whatToDo
    ) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        //  COLUMNS:  id, runtime, youtube trailer array if it exists

        final String MOVIEDB_ID = "id";
        final String MOVIEDB_RUNTIME = "runtime";
        final String MOVIEDB_TRAILERS = "trailers";
        final String MOVIEDB_YOUTUBE_TRAILERS = "youtube";
        final String TRAILER_NAME = "name";
        final String TRAILER_SOURCE = "source";
        final String MOVIEDB_REVIEWS = "reviews";
        final String MOVIEDB_REVIEWS_RESULTS = "results";
        final String REVIEW_NAME = "author";
        final String REVIEW_SOURCE = "url";


        int inserted;

        /*
         *  pull the flat info from JSON, and pull only the trailer array,
         *  then inspect it for any youtube trailers
         */
        try {
            JSONObject movieInfoJson = new JSONObject(movieInfoJsonStr);
            JSONObject movieInfoTrailersJson = movieInfoJson.getJSONObject(MOVIEDB_TRAILERS);
            JSONArray movieYoutubeTrailerArray = movieInfoTrailersJson.getJSONArray(MOVIEDB_YOUTUBE_TRAILERS);
            JSONObject movieInfoReviewsJson = movieInfoJson.getJSONObject(MOVIEDB_REVIEWS);
            JSONArray movieInfoReviewsArray = movieInfoReviewsJson.getJSONArray(MOVIEDB_REVIEWS_RESULTS);

            // Vector<ContentValues> cVVector = new Vector<ContentValues>(movieYoutubeTrailerArray.length());

            int id;
            String runtime;
            String trailer1_source = "", trailer2_source = "", trailer3_source = "";
            String trailer1_name = "", trailer2_name = "", trailer3_name = "";
            String review1_source = "", review2_source = "", review3_source = "";
            String review1_name = "", review2_name = "", review3_name = "";

            id = movieInfoJson.getInt(MOVIEDB_ID);  // TODO : error check the movie id
            runtime = movieInfoJson.getString(MOVIEDB_RUNTIME);

            ContentValues movieValues = new ContentValues();

            for (int i = 0; i < movieInfoReviewsArray.length(); i++) {
                // Get the JSON object for this review in the list
                JSONObject review = movieInfoReviewsArray.getJSONObject(i);
                String name = review.getString(REVIEW_NAME);
                String source = review.getString(REVIEW_SOURCE);

                if (i == 0) {
                    review1_source = source;
                    review1_name = name;
                }
                else if (i == 1) {
                    review2_source = source;
                    review2_name = name;
                }
                else if (i == 2) {
                    review3_source = source;
                    review3_name = name;
                }
            }

            // it only works to update the record (if it exists) for the movie_id
            // in all *THREE* databases if they are kept consistent.  Fair warning.

            movieValues.put(MoviePopular.COLUMN_REVIEW, review1_source);
            movieValues.put(MoviePopular.COLUMN_REVIEW2, review2_source);
            movieValues.put(MoviePopular.COLUMN_REVIEW3, review3_source);

            movieValues.put(MoviePopular.COLUMN_REVIEW_NAME, review1_name);
            movieValues.put(MoviePopular.COLUMN_REVIEW2_NAME, review2_name);
            movieValues.put(MoviePopular.COLUMN_REVIEW3_NAME, review3_name);
            // flag that all data is now loaded...
            movieValues.put(MoviePopular.COLUMN_DETAILS_LOADED, 1);


            for (int i = 0; i < movieYoutubeTrailerArray.length(); i++) {
                // Get the JSON object for this trailer in the list
                JSONObject trailer = movieYoutubeTrailerArray.getJSONObject(i);
                String name = trailer.getString(TRAILER_NAME);
                String source = trailer.getString(TRAILER_SOURCE);

                if (i == 0) {
                    trailer1_source = source;
                    trailer1_name = name;
                }
                else if (i == 1) {
                    trailer2_source = source;
                    trailer2_name = name;
                }
                else if (i == 2) {
                    trailer3_source = source;
                    trailer3_name = name;
                }

            }

            movieValues.put(MoviePopular.COLUMN_RUNTIME, runtime);
            movieValues.put(MoviePopular.COLUMN_TRAILER, trailer1_source);
            movieValues.put(MoviePopular.COLUMN_TRAILER2, trailer2_source);
            movieValues.put(MoviePopular.COLUMN_TRAILER3, trailer3_source);

            movieValues.put(MoviePopular.COLUMN_TRAILER_NAME, trailer1_name);
            movieValues.put(MoviePopular.COLUMN_TRAILER2_NAME, trailer2_name);
            movieValues.put(MoviePopular.COLUMN_TRAILER3_NAME, trailer3_name);

            inserted = mContext.getContentResolver().update(
                    MoviePopular.buildMovieDetailsUri(id),
                    movieValues,
                    MoviePopular.COLUMN_MOVIE_ID + " = " + String.valueOf(id),
                    null);

            inserted = mContext.getContentResolver().update(
                    MovieTopRated.buildMovieDetailsUri(id),
                    movieValues,
                    MoviePopular.COLUMN_MOVIE_ID + " = " + String.valueOf(id),
                    null);

            inserted = mContext.getContentResolver().update(
                    MovieContract.MovieFavorites.buildMovieDetailsUri(id),
                    movieValues,
                    MoviePopular.COLUMN_MOVIE_ID + " = " + String.valueOf(id),
                    null);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        String whatToDo = params[0];
        String arg = params[1];
        String fetch_string;

        if (whatToDo.contains(FETCH_POPULAR)) {
            fetch_string = "http://api.themoviedb.org/3/movie/popular?page=" + arg;
        } else if (whatToDo.contains(FETCH_TOP_RATED)) {
            fetch_string = "http://api.themoviedb.org/3/movie/top_rated?page=" + arg;
        } else if (whatToDo.contains(FETCH_POPULAR_MOVIE_DETAILS)
                || whatToDo.contains(FETCH_TOP_RATED_MOVIE_DETAILS)) {
            fetch_string =
                    "http://api.themoviedb.org/3/movie/" + arg + "?&append_to_response=trailers,reviews";
        } else {
            throw new RuntimeException(LOG_TAG + "Impossible task here");
        }

        final String APPID = "api_key";
        final String THE_MOVIE_DATABASE_API_KEY = BuildConfig.THE_MOVIE_DATABASE_API_KEY;

        Uri builtUri = Uri.parse(fetch_string).buildUpon()
                .appendQueryParameter(APPID, THE_MOVIE_DATABASE_API_KEY)
                .build();

        Log.i(LOG_TAG, "fetching URL: " + fetch_string);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        try {
            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDb, and open the connection

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            if (whatToDo.contains(FETCH_POPULAR_MOVIE_DETAILS)
                    || whatToDo.contains(FETCH_TOP_RATED_MOVIE_DETAILS)) {
                getMovieDetailsFromJson(movieJsonStr, whatToDo);
            } else {
                getMovieListFromJson(movieJsonStr, whatToDo);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        mTM.nextPopularMovie();

    }
}