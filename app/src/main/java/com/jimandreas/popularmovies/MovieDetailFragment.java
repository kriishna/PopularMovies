package com.jimandreas.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.jimandreas.popularmovies.data.MovieContract.MovieFavorites;
import com.jimandreas.popularmovies.data.MovieContract.MoviePopular;
import com.jimandreas.popularmovies.data.MovieContract.MovieTopRated;
import com.jimandreas.popularmovies.utils.Utility;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  For more information on the architecture used here - the
 *  Advanced Android video sequence hosted by Udacity:
 *
 *  http://www.youtube.com/watch?v=u1webEsoU6w&index=65&list=PLAwxTw4SYaPmETCT07vnDSiIaUBuyut0X
 *  (youtube)
 *
 *  or
 *
 *  https://classroom.udacity.com/courses/ud855/lessons/3940839262/concepts/43314600850923#
 *  (udacity class)
 *
 *  These videos explain the concepts much better than a comment header in this file!
 *
 * See also the Content Provider discussion in the Udacity Android App development class (853).
 */

public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PACKAGE_NAME = "com.jimandreas.popularmovies";
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER_ID = 1;
    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private Calendar mCalendar = new GregorianCalendar();
    private Cursor mCursor = null;
    private TrafficManager mTM = null;
    private String mShareDetails = null;
    private Menu mMenu;

    static final String[] MOVIE_COLUMNS = {
            MoviePopular._ID,
            MoviePopular.COLUMN_MOVIE_ID,
            MoviePopular.COLUMN_POPULAR_INDEX,
            MoviePopular.COLUMN_TOP_RATED_INDEX,
            MoviePopular.COLUMN_OVERVIEW,
            MoviePopular.COLUMN_POSTER_PATH,
            MoviePopular.COLUMN_RELEASE_DATE,
            MoviePopular.COLUMN_RUNTIME,
            MoviePopular.COLUMN_TITLE,
            MoviePopular.COLUMN_VOTE_AVERAGE,
            MoviePopular.COLUMN_VOTE_COUNT,
            MoviePopular.COLUMN_TAGLINE,
            MoviePopular.COLUMN_TRAILER,
            MoviePopular.COLUMN_TRAILER2,
            MoviePopular.COLUMN_TRAILER3,
            MoviePopular.COLUMN_TRAILER_NAME,
            MoviePopular.COLUMN_TRAILER2_NAME,
            MoviePopular.COLUMN_TRAILER3_NAME,
            MoviePopular.COLUMN_FAVORITE,
            MoviePopular.COLUMN_FAVORITE_TIMESTAMP,
            MoviePopular.COLUMN_DETAILS_LOADED,
            MoviePopular.COLUMN_REVIEW,
            MoviePopular.COLUMN_REVIEW2,
            MoviePopular.COLUMN_REVIEW3,
            MoviePopular.COLUMN_REVIEW_NAME,
            MoviePopular.COLUMN_REVIEW2_NAME,
            MoviePopular.COLUMN_REVIEW3_NAME,
            MoviePopular.COLUMN_SPARE_INTEGER,
            MoviePopular.COLUMN_SPARE_STRING1,
            MoviePopular.COLUMN_SPARE_STRING2,
            MoviePopular.COLUMN_SPARE_STRING3,
            MoviePopular.COLUMN_MYNAME

    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_CURSOR_ID = 0;  // required!!   see cursor documentation
    static final int COL_MOVIE_ID = 1;
    static final int COL_POPULAR_INDEX = 2;
    static final int COL_TOP_RATED_INDEX = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_POSTER_PATH = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_RUNTIME = 7;
    static final int COL_TITLE = 8;
    static final int COL_VOTE_AVERAGE = 9;
    static final int COL_VOTE_COUNT = 10;
    static final int COL_TAGLINE = 11;
    static final int COL_TRAILER1 = 12;
    static final int COL_TRAILER2 = 13;
    static final int COL_TRAILER3 = 14;
    static final int COL_TRAILER1_NAME = 15;
    static final int COL_TRAILER2_NAME = 16;
    static final int COL_TRAILER3_NAME = 17;
    static final int COL_FAVORITE = 18;
    static final int COL_FAVORITE_TIMESTAMP = 19;
    static final int COL_DETAILS_LOADED = 20;
    static final int COL_REVIEW = 21;
    static final int COL_REVIEW2 = 22;
    static final int COL_REVIEW3 = 23;
    static final int COL_REVIEW_NAME = 24;
    static final int COL_REVIEW_NAME2 = 25;
    static final int COL_REVIEW_NAME3 = 26;
    static final int COL_SPARE_INTEGER = 27;
    static final int COL_SPARE_STRING1 = 28;
    static final int COL_SPARE_STRING2 = 29;
    static final int COL_SPARE_STRING3 = 30;
    static final int COL_THIS_TABLE_NAME = 31;


    /*
     * The One Grid to Rule them All,
     * and in the funkyness Bind Tem
     */
    private ConstraintLayout mTheMasterGridLayout;
    /*
     * and now all the little minion views.  Do my bidding!!  *I* have the RING of ANDROID.
     */
    private TextView mMDTitleView;
    private ImageView mMDPosterView;
    private TextView mMDYear;
    private TextView mMDRunningtime;
    private TextView mMDRatingAndVotes;
    private TextView mMDSynopsis;

    private ImageView mMDFavoriteButton;

    private TextView mMDTrailers;
    private ImageView mMDPlay1;
    private ImageView mMDPlay2;
    private ImageView mMDPlay3;

    private TextView mMDTrailer1;
    private TextView mMDTrailer2;
    private TextView mMDTrailer3;

    private TextView mMDReviews;
    private ImageView mMDRev1;
    private ImageView mMDRev2;
    private ImageView mMDRev3;

    private TextView mMovieReview1;
    private TextView mMovieReview2;
    private TextView mMovieReview3;

    private ProgressBar mMDLoadProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        /*
         * Yes yes my master, we have the Precious handle to the Layout!!
         *
         * NOTE: now obsolete - moved to the Constraint system for layout
         */
//        mTheMasterGridLayout = (GridLayout) rootView.findViewById(R.id.movie_detail_grid_layout);
//        mTheMasterGridLayout = (ConstraintLayout)
//                rootView.findViewById(R.id.movie_detail_grid_layout);

        mMDSynopsis = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);
        mMDTitleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
        mMDPosterView = (ImageView) rootView.findViewById(R.id.movie_detail_poster_image);
        mMDYear = (TextView) rootView.findViewById(R.id.movie_detail_year);
        mMDRunningtime = (TextView) rootView.findViewById(R.id.movie_detail_runningtime);
        mMDRatingAndVotes = (TextView) rootView.findViewById(R.id.movie_detail_rating_and_votes);
        mMDSynopsis = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);

        mMDLoadProgress = (ProgressBar) rootView.findViewById(R.id.movie_detail_progress_circle);

        // cache up all the Trailer and Review UX views - 
        //   these will get toggled into visibility based on their 
        // rather random presence in TheMovieDB...

        mMDTrailers = (TextView) rootView.findViewById(R.id.movie_detail_trailers);
        mMDPlay1 = (ImageView) rootView.findViewById(R.id.movie_detail_trailer_play_button);
        mMDPlay2 = (ImageView) rootView.findViewById(R.id.movie_detail_trailer_play_button2);
        mMDPlay3 = (ImageView) rootView.findViewById(R.id.movie_detail_trailer_play_button3);
        mMDTrailer1 = (TextView) rootView.findViewById(R.id.movie_detail_trailer1);
        mMDTrailer2 = (TextView) rootView.findViewById(R.id.movie_detail_trailer2);
        mMDTrailer3 = (TextView) rootView.findViewById(R.id.movie_detail_trailer3);
        mMDTrailer1.setOnClickListener(mTrailerClickListener);
        mMDTrailer2.setOnClickListener(mTrailerClickListener);
        mMDTrailer3.setOnClickListener(mTrailerClickListener);

        mMDReviews = (TextView) rootView.findViewById(R.id.movie_detail_reviews);
        mMDRev1 = (ImageView) rootView.findViewById(R.id.play_rev1);
        mMDRev2 = (ImageView) rootView.findViewById(R.id.play_rev2);
        mMDRev3 = (ImageView) rootView.findViewById(R.id.play_rev3);

        mMovieReview1 = (TextView) rootView.findViewById(R.id.movie_review1);
        mMovieReview2 = (TextView) rootView.findViewById(R.id.movie_review2);
        mMovieReview3 = (TextView) rootView.findViewById(R.id.movie_review3);
        mMovieReview1.setOnClickListener(mReviewClickListener);
        mMovieReview2.setOnClickListener(mReviewClickListener);
        mMovieReview3.setOnClickListener(mReviewClickListener);

        mMDFavoriteButton = (ImageView) rootView.findViewById(R.id.movie_detail_favorite_imageviewbutton);
        mMDFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int favorite_state;

//                Snackbar.make(v, "clicky", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();

                if (mCursor == null || !mCursor.moveToFirst()) {
                    return;
                }

                // pull the favorite table entry if it exists
                int movie_id = mCursor.getInt(COL_MOVIE_ID);
                Uri uri = MovieFavorites.buildMovieDetailsUri(movie_id);
                Cursor fav_cursor = getContext().getContentResolver().query(
                        uri, null, null, null, null);

                // debugging
                ContentValues testValues = new ContentValues();
                if (fav_cursor.moveToFirst()) {
                    DatabaseUtils.cursorRowToContentValues(fav_cursor, testValues);
                }

                // dump in the current movie table entry into the new
                // favorite row info
                ContentValues favoriteValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(mCursor, favoriteValues);

                // OK if the favorite row already exists, then toggle its state
                // otherwise set it to "1" or true - it IS a favorite now
                int fav_status = 0;
                if (fav_cursor.moveToFirst()) {
                    fav_status = fav_cursor.getInt(COL_FAVORITE);
                }
                favoriteValues.put(MovieFavorites.COLUMN_MYNAME, "favorites");

                if (fav_status == 0) {
                    favorite_state = 1;
                    favoriteValues.remove(MovieFavorites.COLUMN_FAVORITE);
                    favoriteValues.put(MovieFavorites.COLUMN_FAVORITE, 1);
                    favoriteValues.put(MovieFavorites.COLUMN_FAVORITE_TIMESTAMP, mCalendar.getTimeInMillis());
                    Log.d(LOG_TAG, "favorites - added " + movie_id + " to favorites");
                } else {
                    favorite_state = 0;
                    favoriteValues.remove(MovieFavorites.COLUMN_FAVORITE);
                    favoriteValues.put(MovieFavorites.COLUMN_FAVORITE, 0);
                    Log.d(LOG_TAG, "favorites - removed " + movie_id + " from favorites");
                }

                /*
                 * stuff this favorite row into the favorites table
                 * but first remove the _id key or sqlite will crash
                 */
                favoriteValues.remove("_id");

                Uri updated = getContext().getContentResolver().insert(
                        MovieFavorites.CONTENT_URI,
                        favoriteValues
                );

                ContentValues movieValues = new ContentValues();

                if (favorite_state == 1) {
                    mMDFavoriteButton.setImageResource(R.drawable.ic_favorite_selected);
                    movieValues.put(MoviePopular.COLUMN_FAVORITE, 1);
                    mTM.addFavoriteID(movie_id);
                } else {
                    mMDFavoriteButton.setImageResource(R.drawable.ic_favorite_unselected);
                    movieValues.put(MoviePopular.COLUMN_FAVORITE, 0);
                    mTM.removeFavoriteID(movie_id);
                }

                /*
                 * do an update selecting on the movie_id in BOTH popular and top_rated
                 * tables.   The same movie_id might be in both tables.
                 */
                String myMode = MoviePopular.getMovieModeFromUri(mUri);
                int result;

                uri = MoviePopular.buildMovieDetailsUri(movie_id);
                result = getContext().getContentResolver().update(
                        uri,
                        movieValues,
                        MoviePopular.COLUMN_MOVIE_ID + " = " + String.valueOf(movie_id),
                        null);

                uri = MovieTopRated.buildMovieDetailsUri(movie_id);
                result = getContext().getContentResolver().update(
                        uri,
                        movieValues,
                        MoviePopular.COLUMN_MOVIE_ID + " = " + String.valueOf(movie_id),
                        null);
            }
        });

        mTM = TrafficManager.getInstance(getContext());

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() instanceof MovieDetailActivity) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.moviedetailfragment, menu);
            mMenu = menu;
        } else {
            inflater.inflate(R.menu.moviedetailfragment, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        /*
         * See Audacity Advanced Android training for more info on this
         * rather nice handling of visibility:
         *
         *  So Much Real Estate Part 3 (solution)
         * https://www.youtube.com/watch?v=jkK-Uxx6dLQ
         *
         * Basically the fragment is parented to a CardView when
         * running on a Tablet, in Landscape or Portrait orientation.
         * This nulls out the simultaneous presentation of the detail view
         * before the info is retrieved in the cursor.
         */
        ViewParent vp = getView().getParent();
        if (vp != null && vp instanceof CardView) {
            ((View) vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mCursor = cursor;
            ViewParent vp = getView().getParent();
            if (vp instanceof CardView) {
                ((View) vp).setVisibility(View.VISIBLE);
            }

            String movie_title = cursor.getString(COL_TITLE);
            mMDTitleView.setText(movie_title);
            mMDTitleView.setContentDescription(movie_title);

            String movie_poster_path = cursor.getString(COL_POSTER_PATH);
            Picasso.with(getContext())
                    .load("http://image.tmdb.org/t/p/w185/" + movie_poster_path)
                    .into(mMDPosterView);

//            Glide.with(getContext())
//                    .load("http://image.tmdb.org/t/p/w185/" + movie_poster_path)
//                    // .fitCenter()
//
//                    .error(R.drawable.ic_no_wifi)
//                    .override(100, 150)
//                    // .fitCenter()
//                    // .centerCrop()   // results in cropping - not acceptable
//                    .dontAnimate()  // this causes jank if used instead of crossFade
//                    // .crossFade(1)
//                    .placeholder(R.drawable.ic_loading)

//                    .into(mMDPosterView);


            String release_date = cursor.getString(COL_RELEASE_DATE);
            mMDYear.setText(release_date);
            mMDYear.setContentDescription("The Movie was released on " + release_date);

            String synopsis = cursor.getString(COL_OVERVIEW);
            mMDSynopsis.setText(synopsis);
            mMDSynopsis.setContentDescription(synopsis);

            String vote_average = cursor.getString(COL_VOTE_AVERAGE);
            String vote_count = cursor.getString(COL_VOTE_COUNT);
            mMDSynopsis.setContentDescription(
                    "The movie is rated " + vote_average + " from " + vote_count + " votes");

            String combo = vote_average + "/10 (" + vote_count + ")";
            mMDRatingAndVotes.setText(combo);
            mMDRatingAndVotes.setContentDescription("\u00A0");

            /*
             * check the favorites boolean status and display the
             * matching heart.
             */
            int favorite_state = cursor.getInt(COL_FAVORITE);
            if (favorite_state == 1) {
                mMDFavoriteButton.setImageResource(R.drawable.ic_favorite_selected);
                mMDFavoriteButton.setContentDescription("It is set to a favorite movie");
            } else {
                mMDFavoriteButton.setImageResource(R.drawable.ic_favorite_unselected);
                mMDFavoriteButton.setContentDescription("The favorite is not set");
            }

            mMDPlay1.setContentDescription("Play trailer");
            mMDPlay2.setContentDescription("Play trailer");
            mMDPlay3.setContentDescription("Play trailer");
            mMDRev1.setContentDescription("Read review");
            mMDRev2.setContentDescription("Read review");
            mMDRev3.setContentDescription("Read review");

            /*
             * if all the extra data hasn't been loaded yet,
             * then post a circle flavored "progress bar"
             * and initiate the task to download the data.
             */
            Integer movie_details_loaded = cursor.getInt(COL_DETAILS_LOADED);
            if (movie_details_loaded == 0) {

                mMDLoadProgress.setVisibility(View.VISIBLE);
                /*
                 * start a task to fetch the movie details in the background, and update the movie
                 * table in the ContentProvider with the details
                 */
                FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getActivity());
                String myname = cursor.getString(COL_THIS_TABLE_NAME);
                Integer movie_id = cursor.getInt(COL_MOVIE_ID);
                String todo;
                if (myname.contains("popular")) {
                    todo = fetchMovieListTask.FETCH_POPULAR_MOVIE_DETAILS;
                } else {
                    todo = fetchMovieListTask.FETCH_TOP_RATED_MOVIE_DETAILS;
                }
                fetchMovieListTask.execute(
                        todo,
                        String.valueOf(movie_id));

                /*
                 * whack all the elements (trailers and reviews)
                 * for which we don't have yet data...
                 */
                setGoneStatus();

                /*
                 * invoke the Power of the Android Ring!
                 */
//                mTheMasterGridLayout.invalidate();
//                mTheMasterGridLayout.setRowCount(10);  //  oops didn't work!

                mTM.buildPopularMoviesListToUpdate();
                // no more work to do now
                return;
            }

            /*
             * hey we have data!
             */
            mMDLoadProgress.setVisibility(View.INVISIBLE);
            /*
             * The Indeterminate Ring of Waiting has been banished!
             * Bring back the minions to do our detail bidding!
             */

            // TODO: try swapping two different GridLayouts - one with the Trailers and Reviews
//            mTheMasterGridLayout.invalidate();
//            mTheMasterGridLayout.setRowCount(25);

            // TODO: figure out formatted string for running time for translation

            String running_time = cursor.getString(COL_RUNTIME);
            if (running_time != null) {
                mMDRunningtime.setText(running_time + " minutes");
            }

            String trailer1_name = cursor.getString(COL_TRAILER1_NAME);
            String trailer2_name = cursor.getString(COL_TRAILER2_NAME);
            String trailer3_name = cursor.getString(COL_TRAILER3_NAME);

            if (trailer1_name.length() != 0) {
                mMDTrailer1.setText(trailer1_name);
                mMDTrailer1.setVisibility(View.VISIBLE);
                mMDPlay1.setVisibility(View.VISIBLE);
                mMDTrailers.setVisibility(View.VISIBLE); // don't forget the section header!
            }
            if (trailer2_name.length() != 0) {
                mMDTrailer2.setText(trailer2_name);
                mMDTrailer2.setVisibility(View.VISIBLE);
                mMDPlay2.setVisibility(View.VISIBLE);
            }
            if (trailer3_name.length() != 0) {
                mMDTrailer3.setText(trailer3_name);
                mMDTrailer3.setVisibility(View.VISIBLE);
                mMDPlay3.setVisibility(View.VISIBLE);
            }

            int col = cursor.getColumnIndex(MoviePopular.COLUMN_REVIEW_NAME);
            int col2 = COL_REVIEW_NAME;

            String review1_name = cursor.getString(COL_REVIEW_NAME);
            String review2_name = cursor.getString(COL_REVIEW_NAME2);
            String review3_name = cursor.getString(COL_REVIEW_NAME3);

            if (review1_name.length() != 0) {
                mMovieReview1.setText(review1_name);
                mMovieReview1.setVisibility(View.VISIBLE);
                mMDReviews.setVisibility(View.VISIBLE);
                mMDRev1.setVisibility(View.VISIBLE);
            }
            if (review2_name.length() != 0) {
                mMovieReview2.setText(review2_name);
                mMovieReview2.setVisibility(View.VISIBLE);
                mMDRev2.setVisibility(View.VISIBLE);
            }
            if (review3_name.length() != 0) {
                mMovieReview3.setText(review3_name);
                mMovieReview3.setVisibility(View.VISIBLE);
                mMDRev3.setVisibility(View.VISIBLE);
            }
        }

        String movie_title = mCursor.getString(COL_TITLE);
        String trailer1_name = mCursor.getString(COL_TRAILER1_NAME);
        String url1 = mCursor.getString(COL_TRAILER1);

        // TODO: isolate strings for localization
        String prefix = "http://www.youtube.com/watch?v=";

        mShareDetails = "Here is an interesting movie!\n" +
                movie_title + "\nand here is a youtube video to check out:\n" +
                trailer1_name + "\n" + prefix + url1;

        if (mMenu != null) {
            MenuItem menuItem = mMenu.findItem(R.id.action_share);
            if (menuItem != null) {
                menuItem.setIntent(createShareForecastIntent());
            }
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        if (mShareDetails == null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing a favorite movie info...");
            return shareIntent;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareDetails);
        return shareIntent;
    }

    // handle clicks on Reviews - view the URL in the review

    private View.OnClickListener mReviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = null;
            if (mCursor == null) {
                return;
            }
            if (v == mMovieReview1) {
                url = mCursor.getString(COL_REVIEW);
            } else if (v == mMovieReview2) {
                url = mCursor.getString(COL_REVIEW2);
            } else if (v == mMovieReview3) {
                url = mCursor.getString(COL_REVIEW3);
            }
            if (url != null) {
                Utility.viewUrl(url, getContext());
            }
        }
    };

    // handle clicks on Trailers - view the Youtube URL

    private View.OnClickListener mTrailerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = null;
            if (mCursor == null) {
                return;
            }
            if (v == mMDTrailer1) {
                url = mCursor.getString(COL_TRAILER1);
            } else if (v == mMDTrailer2) {
                url = mCursor.getString(COL_TRAILER2);
            } else if (v == mMDTrailer3) {
                url = mCursor.getString(COL_TRAILER3);
            }
            if (url != null) {
                Utility.watchYoutubeVideo(url, getContext());
            }
        }
    };

    /*
     * Woohoo making license plates (http://astrofish.net/blog/cryptonomicon/)
     * Tough job but *somebody's* got to do it!!   Or the results WILL NOT be pretty :-)
     */

    private void setGoneStatus() {
        mMDTrailers.setVisibility(View.GONE);
        mMDPlay1.setVisibility(View.GONE);
        mMDPlay2.setVisibility(View.GONE);
        mMDPlay3.setVisibility(View.GONE);
        mMDTrailer1.setVisibility(View.GONE);
        mMDTrailer2.setVisibility(View.GONE);
        mMDTrailer3.setVisibility(View.GONE);

        mMDReviews.setVisibility(View.GONE);
        mMDRev1.setVisibility(View.GONE);
        mMDRev2.setVisibility(View.GONE);
        mMDRev3.setVisibility(View.GONE);

        mMovieReview1.setVisibility(View.GONE);
        mMovieReview2.setVisibility(View.GONE);
        mMovieReview3.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mShareDetails == null) {
            return super.onOptionsItemSelected(item);
        }
//        if (getActivity() instanceof MovieDetailActivity) {
//            return super.onOptionsItemSelected(item);
//        }
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mShareDetails);
            this.startActivity(shareIntent);
        }


//        if (id == R.id.action_refresh) {
//
//            // TODO : get status of popular or top_rated...
//            FetchMovieListTask mFetchMovieListTask = new FetchMovieListTask(this);
//            mFetchMovieListTask.execute(
//                    mFetchMovieListTask.FETCH_POPULAR, "1");
//            return true;
//        }
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}