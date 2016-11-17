package com.jimandreas.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntDef;
import timber.log.Timber;

import com.jimandreas.popularmovies.data.MovieContract;
import com.jimandreas.popularmovies.data.MovieLoadDetails;
import com.jimandreas.popularmovies.data.MovieLoadFavorites;
import com.jimandreas.popularmovies.utils.Utility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Traffic Manager class
 * <p/>
 * set up as a Singleton class to solve the following problems App-wide:
 * <p/>
 * 1) provides a simple display mode tracking for  { popular, top_rated, or favorites }
 * <p/>
 * 2) manages the tracking of how many pages of movie data have been downloaded
 * in each of the popular or top_rated views
 * <p/>
 * 3) keeping track of the simple list of favorite movie ids for the various fragments that
 * need to check them for UX purposes.
 * <p/>
 * 4) does read-ahead of movie details for popular movies.  (not top_rated as yet - for performance
 * comparison / experimental reasons)
 * <p/>
 * for more information on Android recommended Singleton Patterns, as opposed to subclassing
 * the Application class (not recommended practice) - see this link:
 * <p/>
 * http://developer.android.com/training/volley/requestqueue.html#singleton
 * <p/>
 * and List interface:
 * https://docs.oracle.com/javase/tutorial/collections/interfaces/list.html
 */
public class TrafficManager {
    private static final String LOG_TAG = TrafficManager.class.getSimpleName();
    private static TrafficManager mInstance;
    private Context mContext;
    private MovieLoadFavorites mLoadFavorites;
    private MovieLoadDetails mLoadDetails;
    private static Iterator mIterator;

    private TrafficManager(Context context) {
        mContext = context;
    }

    public static synchronized TrafficManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TrafficManager(context);
            /*
             * init Timber here... One time only!
             */
            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree());
            }
        }
        return mInstance;
    }

    /**
     * Modality of the app- which type of movies are displayed?
     */
    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVORITES = 2;
    @IntDef( {POPULAR, TOP_RATED, FAVORITES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayMode {}

    public void setDisplayMode(@DisplayMode int displayMode) {
        mTMdisplayMode = displayMode;
    }

    @TrafficManager.DisplayMode
    int mTMdisplayMode = POPULAR;

    @TrafficManager.DisplayMode
    public int getDisplayMode() {
        return mTMdisplayMode;
    }

    private String mDetailDisplayMode = null;

    public String getDetailDisplayMode() {
        return mDetailDisplayMode;
    }

    public void setDetailDisplayMode(String mDisplayMode) {
        this.mDetailDisplayMode = mDisplayMode;
    }

    /*
     * track pages of movies downloaded,
     * as it is possible for the PopularMovieFragment to be destroyed!
     */
    private int mPopularMoviesPage = 0;
    private int mTopRatedMoviesPage = 0;

    public int getTopRatedMoviesPageNumber() {
        return mTopRatedMoviesPage;
    }

    public void setTopRatedMoviesPageNumber(int mTopRatedMoviesPage) {
        this.mTopRatedMoviesPage = mTopRatedMoviesPage;
    }

    public int getPopularMoviesPageNumber() {
        return mPopularMoviesPage;
    }

    public void setPopularMoviesPageNumber(int mPopularMoviesPage) {
        this.mPopularMoviesPage = mPopularMoviesPage;
    }

    public int calculatePopularMoviesPageNumber() {
        Uri uri = MovieContract.MoviePopular.buildPopularMoviesUri();
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                new String[]{
                        MovieContract.MoviePopular.COLUMN_MOVIE_ID,
                },
                null,
                null,
                null,
                null);

        // if no movies in the database, then start with loading
        // page 1 from the movie DB.
        //  TODO: assumption is that there are 20 movies per page
        // answer : no there are duplicate IDs!   Patch for now.
        //  anyway to verify this up front?

        if (cursor == null || !cursor.moveToFirst()) {
            return 0;
        }
        int count = cursor.getCount();
        if ((count % 20) != 0) {
            Timber.i("there are duplicate movie_ids!! Ugh.  Patching.");
            count = count+19;
        }
        mPopularMoviesPage = count / 20;
        cursor.close();
        Timber.i("Popular page count = " + mPopularMoviesPage  + ", count is " + count);
        return (mPopularMoviesPage);
    }

    public int calculateTopRatedMoviesPageNumber() {
        Uri uri = MovieContract.MovieTopRated.buildTopRatedMoviesUri();
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                new String[]{
                        MovieContract.MovieTopRated.COLUMN_MOVIE_ID,
                },
                null,
                null,
                null,
                null);

        // if no movies in the database, then start with loading
        // page 1 from the movie DB.
        //  TODO: assumption is that there are 20 movies per page
        // answer : no there are duplicate IDs!   Patch for now.
        //  anyway to verify this up front?

        if (cursor == null || !cursor.moveToFirst()) {
            return 0;
        }
        int count = cursor.getCount();
        if ((count % 20) != 0) {
            Timber.i("there are duplicate movie_ids!! Ugh.  Patching.");
            count = count+19;
        }
        mTopRatedMoviesPage = count / 20;
        cursor.close();
        Timber.i("Top rated page count = " + mTopRatedMoviesPage + ", count is " + count);
        return (mTopRatedMoviesPage);
    }

    // favorites tracking
    private static HashSet<Integer> mFavoritesSet = new HashSet<>();
    // popular movie details update tracking
    private static HashSet<Integer> mPopularUpdatePending = null;




    /*
     * 3) manage list of favorited movie ids.
     *
     * doesn't look like much does it.
     * but the meat of this method is to call "MovieLoadFavorites"
     * buried in the data/MovieLoadFavorites class.   This class
     * walks the favorites table, and tosses anything that still isn't marked.
     */

    public void addFavoriteID(int movie_id) {
        mFavoritesSet.add(movie_id);
    }

    public void removeFavoriteID(int movie_id) {
        mFavoritesSet.remove(movie_id);
    }

    public Boolean checkFavoriteID(int movie_id) {
        return mFavoritesSet.contains(movie_id);
    }

    public int numFavorites() {
        return mFavoritesSet.size();
    }

    public void setScrolling(Boolean mScrolling) {
        this.mScrolling = mScrolling;
    }

    private Boolean mScrolling = false;

    public void validateFavorites() {
        Iterator iterator = mFavoritesSet.iterator();
        mLoadFavorites = new MovieLoadFavorites(mContext);
        mLoadFavorites.execute("do this");

//        while ( iterator.hasNext() )
//            Timber.i("   " + iterator.next());
    }

    /*
     * 4) popular movies - schedule tasks to read-ahead the movie details.
     *    Cancel the read-ahead if the list is scrolled.
     */
    // TODO: extend detail read-ahead to Top Rated movies
    /*
     * 1) build the list (called from FetchMovieListTask and MovieDetailFragment)
     * 2) the list is built in mPopularUpdatePending
     * 3) the list is iterated, calling back into FetchMovieListTask
     */
    public void buildPopularMoviesListToUpdate() {
        if (!Utility.isNetworkAvailable(mContext)) {
            return;
        }
/*
 * turn off for now.
 */
        if (Utility.isNetworkAvailable(mContext)) {
            return;
        }

        // check to see if there is an update in progress
        if (mPopularUpdatePending != null) {
            return;
        }
        mPopularUpdatePending = new HashSet<>();
        // mPopularUpdatePending.clear();
        mLoadDetails = new MovieLoadDetails(mContext);
        mLoadDetails.execute("do this");
    }

    public void addPopularMovieToUpdate(int movie_id) {
        mPopularUpdatePending.add(movie_id);
    }

    /*
     * set up the FetchMovieListTask in a mode where it will
     * iterate through our movies that need details
     */
    public void iteratePopularMoviesList() {

        mIterator = mPopularUpdatePending.iterator();
        if (!mIterator.hasNext()) {
            mIterator = null;
            return;
        }

        int movie_id = (int) mIterator.next();

        FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(mContext);
        fetchMovieListTask.execute(
                fetchMovieListTask.FETCH_POPULAR_MOVIE_DETAILS,
                String.valueOf(movie_id));
    }

    public void nextPopularMovie() {
        if (mIterator == null) {
            return;
        }
        /*
         * all done.  ready for the next round of work!
         */
        if (!mIterator.hasNext()) {
            mIterator = null;
            mPopularUpdatePending = null;
            return;
        }
        // cancel the update iteration if the list is scrolling
        if (mScrolling) {
            mIterator = null;
            mPopularUpdatePending = null;
            return;
        }
        int movie_id = (int) mIterator.next();

        FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(mContext);
        fetchMovieListTask.execute(
                fetchMovieListTask.FETCH_POPULAR_MOVIE_DETAILS,
                String.valueOf(movie_id));

    }


    private  int mPopularPosition = -1;
    private  int mTopratedPosition = -1;
    private  int mFavoritesPosition = -1;

    public  int getPopularPosition() {
        return mPopularPosition;
    }

    public  void setPopularPosition(int position) {
        mPopularPosition = position;
    }

    public  int getTopratedPosition() {
        return mTopratedPosition;
    }

    public  void setTopratedPosition(int position) {
        mTopratedPosition = position;
    }

    public  int getFavoritesPosition() {
        return mFavoritesPosition;
    }

    public  void setFavoritesPosition(int position) {
        mFavoritesPosition = position;
    }
}
