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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.jimandreas.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_MOVIE = "movie";

    //  COLUMNS:  id, overview, poster_path, release date, title, vote_average, vote_count (detail:  trailers)

    public static final class MoviePopular implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POPULAR_INDEX = "popular_index";
        public static final String COLUMN_TOP_RATED_INDEX = "top_rated_index";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_TRAILER2 = "trailer2";
        public static final String COLUMN_TRAILER3 = "trailer3";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER2_NAME = "trailer2_name";
        public static final String COLUMN_TRAILER3_NAME = "trailer3_name";
        public static final String COLUMN_FAVORITE = "favorite_flag";
        public static final String COLUMN_FAVORITE_TIMESTAMP = "favorite_timestamp";
        public static final String COLUMN_DETAILS_LOADED = "details_loaded";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_REVIEW2 = "review2";
        public static final String COLUMN_REVIEW3 = "review3";
        public static final String COLUMN_REVIEW_NAME = "review_name";
        public static final String COLUMN_REVIEW2_NAME = "review2_name";
        public static final String COLUMN_REVIEW3_NAME = "review3_name";
        public static final String COLUMN_SPARE_INTEGER = "spare_integer";
        public static final String COLUMN_SPARE_STRING1 = "spare_string1";
        public static final String COLUMN_SPARE_STRING2 = "spare_string2";
        public static final String COLUMN_SPARE_STRING3 = "spare_string3";
        public static final String COLUMN_MYNAME = "myname";



        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
//        public static final Uri CONTENT_MOVIE_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_POPULAR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String CONTENT_POPULAR_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_POPULAR + "/" + PATH_MOVIE;

        public static Uri buildMovieDetailsUri(long id) {
            return ContentUris.withAppendedId(buildMovieDetails(), id);
        }

        public static Uri buildPopularMoviesUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_POPULAR)
                    .build();
        }

        public static Uri buildMovieDetails() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_POPULAR)
                    .appendPath(PATH_MOVIE)
                    .build();
        }

        // these are pretty dependent on the structure of the URI
        //    ... / <mode> / movie / idnumber   in general here
        //   so:  item 0 is mode, item 2 is movie idnumber
        //  yes it is a bit of a hack but that is what we have here for lego pieces
        //
        public static String getMovieModeFromUri(Uri uri) {
            return uri.getPathSegments().get(0);
        }
        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }
    public static final class MovieTopRated implements BaseColumns {

        public static final String TABLE_NAME = "top_rated";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POPULAR_INDEX = "popular_index";
        public static final String COLUMN_TOP_RATED_INDEX = "top_rated_index";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_TRAILER2 = "trailer2";
        public static final String COLUMN_TRAILER3 = "trailer3";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER2_NAME = "trailer2_name";
        public static final String COLUMN_TRAILER3_NAME = "trailer3_name";
        public static final String COLUMN_FAVORITE = "favorite_flag";
        public static final String COLUMN_FAVORITE_TIMESTAMP = "favorite_timestamp";
        public static final String COLUMN_DETAILS_LOADED = "details_loaded";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_REVIEW2 = "review2";
        public static final String COLUMN_REVIEW3 = "review3";
        public static final String COLUMN_REVIEW_NAME = "review_name";
        public static final String COLUMN_REVIEW2_NAME = "review2_name";
        public static final String COLUMN_REVIEW3_NAME = "review3_name";
        public static final String COLUMN_SPARE_INTEGER = "spare_integer";
        public static final String COLUMN_SPARE_STRING1 = "spare_string1";
        public static final String COLUMN_SPARE_STRING2 = "spare_string2";
        public static final String COLUMN_SPARE_STRING3 = "spare_string3";
        public static final String COLUMN_MYNAME = "myname";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
//        public static final Uri CONTENT_MOVIE_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TOP_RATED_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String CONTENT_TOP_RATED_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_TOP_RATED + "/" + PATH_MOVIE;

        public static Uri buildMovieDetailsUri(long id) {
            return ContentUris.withAppendedId(buildMovieDetails(), id);
        }

        public static Uri buildTopRatedMoviesUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TOP_RATED)
                    .build();
        }

        public static Uri buildMovieDetails(String movie_id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(movie_id)
                    .build();
        }

        public static Uri buildMovieDetails() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TOP_RATED)
                    .appendPath(PATH_MOVIE)
                    .build();
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class MovieFavorites implements BaseColumns {

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POPULAR_INDEX = "popular_index";
        public static final String COLUMN_TOP_RATED_INDEX = "top_rated_index";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_TRAILER2 = "trailer2";
        public static final String COLUMN_TRAILER3 = "trailer3";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER2_NAME = "trailer2_name";
        public static final String COLUMN_TRAILER3_NAME = "trailer3_name";
        public static final String COLUMN_FAVORITE = "favorite_flag";
        public static final String COLUMN_FAVORITE_TIMESTAMP = "favorite_timestamp";
        public static final String COLUMN_DETAILS_LOADED = "details_loaded";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_REVIEW2 = "review2";
        public static final String COLUMN_REVIEW3 = "review3";
        public static final String COLUMN_REVIEW_NAME = "review_name";
        public static final String COLUMN_REVIEW2_NAME = "review2_name";
        public static final String COLUMN_REVIEW3_NAME = "review3_name";
        public static final String COLUMN_SPARE_INTEGER = "spare_integer";
        public static final String COLUMN_SPARE_STRING1 = "spare_string1";
        public static final String COLUMN_SPARE_STRING2 = "spare_string2";
        public static final String COLUMN_SPARE_STRING3 = "spare_string3";
        public static final String COLUMN_MYNAME = "myname";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_FAVORITES_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String CONTENT_FAVORITES_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_FAVORITES;

        public static Uri buildMovieDetailsUri(long id) {
            return ContentUris.withAppendedId(buildMovieDetails(), id);
        }

        public static Uri buildFavoriteMoviesUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITES)
                    .build();
        }

        public static Uri buildMovieFavoritesWithID(String movie_id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITES)
                    .appendPath(movie_id)
                    .build();
        }

        public static Uri buildMovieDetails() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITES)
                    .appendPath(PATH_MOVIE)
                    .build();
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);  // see comment earlier
        }
    }
}
