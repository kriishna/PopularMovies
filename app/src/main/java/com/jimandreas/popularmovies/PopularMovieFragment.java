package com.jimandreas.popularmovies;

/**
 * amazingly good article on the RecyclerView here:
 * https://guides.codepath.com/android/using-the-recyclerview
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.jimandreas.popularmovies.data.MovieContract.MovieFavorites;
import com.jimandreas.popularmovies.data.MovieContract.MoviePopular;
import com.jimandreas.popularmovies.data.MovieContract.MovieTopRated;
import com.jimandreas.popularmovies.utils.Utility;

import java.util.Vector;

import static com.jimandreas.popularmovies.TrafficManager.FAVORITES;
import static com.jimandreas.popularmovies.TrafficManager.POPULAR;
import static com.jimandreas.popularmovies.TrafficManager.TOP_RATED;

public class PopularMovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PopularMovieFragment.class.getSimpleName();
    private final Boolean EXTRA_VERBOSE = false;
    private static final int LOADER_ID = 0;
    private TrafficManager mTM = null;

    /*
     * keep this consistent between POPULAR and TOP_RATED - makes things cleaner
     * and then can be shared
     */
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


    // private ArrayAdapter<String> mForecastAdapter;
    private MovieAdapter mMovieAdapter;
    private Bundle mSavedInstanceState = new Bundle();
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String MOVIE_DATA_PAGE_NUMBER = "Page";
    private RecyclerView mRecyclerView;
    private boolean mAutoSelectView;

//    private String mDisplayMode = DISPLAY_POPULAR;

    public static String DISPLAY_TAG = "displaytag";
//    public static String DISPLAY_POPULAR = "popular";
//    public static String DISPLAY_TOP_RATED = "toprated";
//    public static String DISPLAY_FAVORITES = "favorites";

    /*public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int FAVORITES = 2;
    @IntDef( {POPULAR, TOP_RATED, FAVORITES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayMode {}*/

    @TrafficManager.DisplayMode
    int displayMode = POPULAR;

    private Boolean do_click_on_item_zero = false;

    @TrafficManager.DisplayMode
    public int getMovieDisplayMode() {
        return displayMode;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.  (nice trick !   Seems to be standard usage for inter-fragment
     * communication...)
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri wantMovieDetailsFromThisUri);
    }


    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.PopularMovieFragment,
                0, 0);
        mAutoSelectView = a.getBoolean(R.styleable.PopularMovieFragment_autoSelectView, false);
        a.recycle();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_base, container, false);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        int column_count = getResources().getInteger(R.integer.fragment_movie_detail_column_count);
        GridLayoutManager lm = new GridLayoutManager(getActivity(),
                column_count);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setHasFixedSize(true);
        mTM = TrafficManager.getInstance(getContext());

        View emptyView = rootView.findViewById(R.id.recyclerview_movie_empty);

        mMovieAdapter = new MovieAdapter(getActivity(), new MovieAdapter.MovieAdapterOnClickHandler() {
            /*
             * handle clicking on an adapter position
             * - indicates that the user wants detail on the movie
             */
            @Override
            public void onClick(Long movie_id, MovieAdapter.MovieViewHolder vh) {
                Uri uri;
                String todo;
                mPosition = vh.getAdapterPosition();

                switch (displayMode) {
                    case POPULAR:
                        uri = MoviePopular.buildMovieDetailsUri(movie_id);
                        mTM.setPopularPosition(mPosition);
                        break;
                    case TOP_RATED:
                        uri = MovieTopRated.buildMovieDetailsUri(movie_id);
                        mTM.setTopratedPosition(mPosition);
                        break;
                    case FAVORITES:
                        uri = MovieFavorites.buildMovieDetailsUri(movie_id);
                        mTM.setFavoritesPosition(mPosition);
                        break;
                    default:
                        throw new RuntimeException(LOG_TAG + "display mode is screwed up!!");
                }
//                if (mDisplayMode.contains(DISPLAY_POPULAR)) {
//                    uri = MoviePopular.buildMovieDetailsUri(movie_id);
//                    mTM.setPopularPosition(mPosition);
//                } else if (mDisplayMode.contains(DISPLAY_TOP_RATED)) {
//                    uri = MovieTopRated.buildMovieDetailsUri(movie_id);
//                    mTM.setTopratedPosition(mPosition);
//                } else if (mDisplayMode.contains(DISPLAY_FAVORITES)) {
//                    mTM.setFavoritesPosition(mPosition);
//                    uri = MovieFavorites.buildMovieDetailsUri(movie_id);
//                } else {
//                    throw new RuntimeException(LOG_TAG + "display mode is screwed up!!");
//                }
                
                /*
                 * inform the parent activity that the user would like info on the movie details
                 */
                ((Callback) getActivity())
                        .onItemSelected(uri);

            }
        }, emptyView);

        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        mTM.setScrolling(true);
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        mTM.setScrolling(false);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                String todo = null;

                int offset = mRecyclerView.computeVerticalScrollOffset();
                int height = mRecyclerView.computeVerticalScrollExtent();
                int range = mRecyclerView.computeVerticalScrollRange();
//                Log.d(LOG_TAG, "scrolling offset is " + offset
//                        + " of height " + height
//                        + " of range " + range);

                if (offset + height >= range) {

                    // check for network connectivity

                    if (!Utility.isNetworkAvailable(getContext())) {
                        Snackbar.make(mRecyclerView, R.string.movie_detail_fragment_no_connection, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }
                    Log.i(LOG_TAG, "offset = " + offset + " height = " + height);
                    int page_number = 1;
                    if (EXTRA_VERBOSE) Log.d(LOG_TAG, "**** time to get more data!!");
                    FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getActivity());

                    switch (displayMode) {
                        case POPULAR:
                            todo = fetchMovieListTask.FETCH_POPULAR;
                            page_number = mTM.calculatePopularMoviesPageNumber() + 1;
                            mTM.setPopularMoviesPageNumber(page_number);
                            break;
                        case TOP_RATED:
                            page_number = mTM.calculateTopRatedMoviesPageNumber() + 1;
                            mTM.setTopRatedMoviesPageNumber(page_number);
                            todo = fetchMovieListTask.FETCH_TOP_RATED;
                            break;
                        case FAVORITES:
                            return;  // don't need to fetch favorites from TheMovieDB
                    }
                    fetchMovieListTask.execute(todo, String.valueOf(page_number));
                    Snackbar.make(mRecyclerView, R.string.movie_detail_fragment_fetching_another_page, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
        displayMode = mTM.getDisplayMode();

        FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getActivity());
        String todo = null;
        int the_next_page_of_movies;
        if (displayMode == POPULAR) {
            the_next_page_of_movies = mTM.calculatePopularMoviesPageNumber();
            if (the_next_page_of_movies == 0) {
                the_next_page_of_movies = the_next_page_of_movies + 1;
                mTM.setPopularMoviesPageNumber(the_next_page_of_movies);
                todo = fetchMovieListTask.FETCH_POPULAR;
            }
        } else {
            the_next_page_of_movies = mTM.calculateTopRatedMoviesPageNumber();
            if (the_next_page_of_movies == 0) {
                the_next_page_of_movies = the_next_page_of_movies + 1;
                mTM.setTopRatedMoviesPageNumber(the_next_page_of_movies);
                todo = fetchMovieListTask.FETCH_TOP_RATED;
            }
        }
        if (todo != null) {
            fetchMovieListTask.execute(
                    todo,
                    Integer.toString(the_next_page_of_movies));
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        String sortOrder;

        switch (displayMode) {
            case POPULAR:
                sortOrder = MoviePopular.COLUMN_POPULAR_INDEX + " DESC";
                uri = MoviePopular.buildPopularMoviesUri();
                break;
            case TOP_RATED:
                sortOrder = MovieTopRated.COLUMN_TOP_RATED_INDEX + " DESC";
                uri = MovieTopRated.buildTopRatedMoviesUri();
                break;
            case FAVORITES:
                sortOrder = null;  // TODO: handle favorite sorting by add date
                uri = MovieFavorites.buildFavoriteMoviesUri();
                break;
            default:
                throw new RuntimeException(LOG_TAG + "display mode is out of bounds");
        }

        CursorLoader loader = new CursorLoader(
                getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder
        );
// for debugging - view into the cursor loader contents
        String[] debugString = loader.getProjection();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Vector<ContentValues> cVVector = new Vector<ContentValues>(data.getCount());

            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(data, cv);
                cVVector.add(cv);
            } while (data.moveToNext());

            mMovieAdapter.swapCursor(data);
//            if (mPosition != RecyclerView.NO_POSITION) {
//                mRecyclerView.smoothScrollToPosition(mPosition);
////                mPosition = RecyclerView.NO_POSITION;
//            }
            updateEmptyView();
            /*
             * interesting code left over from the Advanced version of sunshine -
             */

            if (data.getCount() > 0) {
                mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Since we know we're going to get items, we keep the listener around until
                        // we see Children.
                        if (mRecyclerView.getChildCount() > 0) {
                            mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                            // if we are entering Favorites mode and in two pane mode,
                            // then click on item 0 to insure we really have a favorite selected

                            if (do_click_on_item_zero) {
                                int itemPosition = 0;
                                RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(itemPosition);
                                if (vh == null) {  // if this position doesn't exist in the new layout then
                                    itemPosition = 0;
                                    vh = mRecyclerView.findViewHolderForAdapterPosition(itemPosition);
                                }
                                if (null != vh && mAutoSelectView) {
                                    mMovieAdapter.selectView(vh);
//                                    mRecyclerView.smoothScrollToPosition(mPosition);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to RecyclerView.NO_POSITION,
        // so check for that before storing.

        outState.putInt(DISPLAY_TAG, displayMode);
        super.onSaveInstanceState(outState);
    }

    public void updateDisplayMode(@TrafficManager.DisplayMode int mode) {
        int old_position; // debugging
        GridLayoutManager mlm = (GridLayoutManager) mRecyclerView.getLayoutManager();

        // save old scroll position if any
        int position = mlm.findFirstVisibleItemPosition();

        switch (displayMode) {
            case POPULAR:
                mTM.setPopularPosition(position);
                break;
            case TOP_RATED:
                mTM.setTopratedPosition(position);
                break;
            case FAVORITES:
                mTM.setFavoritesPosition(position);
                break;
        }

        displayMode = mode;
        mTM.setDisplayMode(mode);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        String todo;
        old_position = position;

        /*
         * kickstart the movie download if necessary
         */

        switch (displayMode) {
            case POPULAR:
                do_click_on_item_zero = false;
                position = mTM.getPopularPosition();
                if (mTM.calculatePopularMoviesPageNumber() == 0) {
                    position = 0;  // adapter doesn't reset its own view on this mode change
                    do_click_on_item_zero = true;
                    FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getActivity());
                    mTM.setPopularMoviesPageNumber(1);
                    todo = fetchMovieListTask.FETCH_POPULAR;
                    fetchMovieListTask.execute(
                            todo,
                            "1");
                } else if (position == RecyclerView.NO_POSITION) { // on startup first display will not have a position
                    position = 0;
                }
                break;
            case TOP_RATED:
                do_click_on_item_zero = false;
                position = mTM.getTopratedPosition();
                if (mTM.calculateTopRatedMoviesPageNumber() == 0) {
                    position = 0;
                    do_click_on_item_zero = true;
                    FetchMovieListTask fetchMovieListTask = new FetchMovieListTask(getActivity());
                    mTM.setTopRatedMoviesPageNumber(1);
                    todo = fetchMovieListTask.FETCH_TOP_RATED;
                    fetchMovieListTask.execute(
                            todo,
                            "1");
                } else if (position == RecyclerView.NO_POSITION) {
                    position = 0;
                }
                break;
            case FAVORITES:
                do_click_on_item_zero = true;  // don't bother to try to scroll on favorites, do click on item 0
                position = mTM.getFavoritesPosition();
                if (position == RecyclerView.NO_POSITION) {
                    position = 0;
                }
                break;
        }
    mPosition=position;
    Log.i(LOG_TAG,"Change mode, old position = "+old_position+" new position = "+position);
}

    @TrafficManager.DisplayMode public int getDisplayMode() {
        return displayMode;
    }

    /*
       Updates the empty list view with contextually relevant information that the user can
       use to determine why they aren't seeing movie info...  Expand as necessary
    */
    private void updateEmptyView() {
        if (mMovieAdapter.getItemCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.recyclerview_movie_empty);
            if (null != tv) {

                int message = R.string.empty_list;

                if (!Utility.isNetworkAvailable(getActivity())) {
                    message = R.string.empty_list_no_network;
                }
                tv.setText(message);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        displayMode = mTM.getDisplayMode();

        switch (displayMode) {
            case POPULAR:
                mPosition = mTM.getPopularPosition();
                break;
            case TOP_RATED:
                mPosition = mTM.getTopratedPosition();
                break;
            case FAVORITES:
                mPosition = mTM.getFavoritesPosition();
                break;
        }
    }
}
