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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popularmovies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MoviePopular.TABLE_NAME + " (" +
                MovieContract.MoviePopular._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  // REQUIRED!!!
                MovieContract.MoviePopular.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                MovieContract.MoviePopular.COLUMN_POPULAR_INDEX + " INTEGER, " +
                MovieContract.MoviePopular.COLUMN_TOP_RATED_INDEX + " INTEGER, " +
                MovieContract.MoviePopular.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MoviePopular.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MoviePopular.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_RUNTIME + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MoviePopular.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MoviePopular.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                MovieContract.MoviePopular.COLUMN_TAGLINE + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TRAILER + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TRAILER2 + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TRAILER3 + " TEXT, "  +
                MovieContract.MoviePopular.COLUMN_TRAILER_NAME + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TRAILER2_NAME + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_TRAILER3_NAME + " TEXT, "  +
                MovieContract.MoviePopular.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieContract.MoviePopular.COLUMN_FAVORITE_TIMESTAMP + " INTEGER, " +
                MovieContract.MoviePopular.COLUMN_DETAILS_LOADED  + " INTEGER DEFAULT 0, " +
                MovieContract.MoviePopular.COLUMN_REVIEW + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_REVIEW2 + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_REVIEW3 + " TEXT, "  +
                MovieContract.MoviePopular.COLUMN_REVIEW_NAME + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_REVIEW2_NAME + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_REVIEW3_NAME + " TEXT, "  +
                MovieContract.MoviePopular.COLUMN_SPARE_INTEGER  + " INTEGER DEFAULT 0, " +
                MovieContract.MoviePopular.COLUMN_SPARE_STRING1 + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_SPARE_STRING2 + " TEXT, " +
                MovieContract.MoviePopular.COLUMN_SPARE_STRING3 + " TEXT, "  +
                MovieContract.MoviePopular.COLUMN_MYNAME + " TEXT NOT NULL" +

                     " );";

        final String SQL_CREATE_MOVIE_TABLE2 = "CREATE TABLE " + MovieContract.MovieTopRated.TABLE_NAME + " (" +
                MovieContract.MovieTopRated._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  // REQUIRED!!!
                MovieContract.MovieTopRated.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                MovieContract.MovieTopRated.COLUMN_POPULAR_INDEX + " INTEGER, " +
                MovieContract.MovieTopRated.COLUMN_TOP_RATED_INDEX + " INTEGER, " +
                MovieContract.MovieTopRated.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieTopRated.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieTopRated.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_RUNTIME + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieTopRated.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieTopRated.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                MovieContract.MovieTopRated.COLUMN_TAGLINE + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TRAILER + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TRAILER2 + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TRAILER3 + " TEXT, "  +
                MovieContract.MovieTopRated.COLUMN_TRAILER_NAME + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TRAILER2_NAME + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_TRAILER3_NAME + " TEXT, "  +
                MovieContract.MovieTopRated.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieContract.MovieTopRated.COLUMN_FAVORITE_TIMESTAMP + " INTEGER, " +
                MovieContract.MovieTopRated.COLUMN_DETAILS_LOADED  + " INTEGER DEFAULT 0, " +
                MovieContract.MovieTopRated.COLUMN_REVIEW + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_REVIEW2 + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_REVIEW3 + " TEXT, "  +
                MovieContract.MovieTopRated.COLUMN_REVIEW_NAME + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_REVIEW2_NAME + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_REVIEW3_NAME + " TEXT, "  +
                MovieContract.MovieTopRated.COLUMN_SPARE_INTEGER  + " INTEGER DEFAULT 0, " +
                MovieContract.MovieTopRated.COLUMN_SPARE_STRING1 + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_SPARE_STRING2 + " TEXT, " +
                MovieContract.MovieTopRated.COLUMN_SPARE_STRING3 + " TEXT, "  +
                MovieContract.MovieTopRated.COLUMN_MYNAME + " TEXT NOT NULL" +

                " );";

        final String SQL_CREATE_MOVIE_TABLE3 = "CREATE TABLE " + MovieContract.MovieFavorites.TABLE_NAME + " (" +
                MovieContract.MovieFavorites._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +  // REQUIRED!!!

                MovieContract.MovieFavorites.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                MovieContract.MovieFavorites.COLUMN_POPULAR_INDEX + " INTEGER, " +
                MovieContract.MovieFavorites.COLUMN_TOP_RATED_INDEX + " INTEGER, " +
                MovieContract.MovieFavorites.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_RUNTIME + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_TAGLINE + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TRAILER + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TRAILER2 + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TRAILER3 + " TEXT, "  +
                MovieContract.MovieFavorites.COLUMN_TRAILER_NAME + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TRAILER2_NAME + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_TRAILER3_NAME + " TEXT, "  +
                MovieContract.MovieFavorites.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieContract.MovieFavorites.COLUMN_FAVORITE_TIMESTAMP + " INTEGER ," +
                MovieContract.MovieFavorites.COLUMN_DETAILS_LOADED  + " INTEGER DEFAULT 0, " +
                MovieContract.MovieFavorites.COLUMN_REVIEW + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_REVIEW2 + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_REVIEW3 + " TEXT, "  +
                MovieContract.MovieFavorites.COLUMN_REVIEW_NAME + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_REVIEW2_NAME + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_REVIEW3_NAME + " TEXT, "  +
                MovieContract.MovieFavorites.COLUMN_SPARE_INTEGER  + " INTEGER DEFAULT 0, " +
                MovieContract.MovieFavorites.COLUMN_SPARE_STRING1 + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_SPARE_STRING2 + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_SPARE_STRING3 + " TEXT, "  +
                MovieContract.MovieFavorites.COLUMN_MYNAME + " TEXT NOT NULL" +

                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE2);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviePopular.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTopRated.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieFavorites.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
