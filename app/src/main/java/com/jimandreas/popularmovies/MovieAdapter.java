package com.jimandreas.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jimandreas.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private int mPosition = -1;
    final private MovieAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    private TrafficManager mTM = null;
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mPosterView;
        public final ImageView mHeartFlagView;
        public final TextView mDebuggingTextView;
        public final CardView mCardView;
        public final RelativeLayout mListItem;

        public MovieViewHolder(View view) {
            super(view);
            mPosterView = (ImageView) view.findViewById(R.id.movie_poster_imageview);
            mDebuggingTextView = (TextView) view.findViewById(R.id.debugging_textview);
            mHeartFlagView = (ImageView) view.findViewById(R.id.favorite_indicator);
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mListItem = (RelativeLayout) view.findViewById(R.id.list_item_layout);

            view.setOnClickListener(this);
            // added - symantic change in clicking starting at API 23
            mListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            int movie_id_column = mCursor.getColumnIndex(MovieContract.MoviePopular.COLUMN_MOVIE_ID);
            if (movie_id_column == -1) {
                return;
            }
            long movie_id = mCursor.getLong(movie_id_column);
            mClickHandler.onClick(movie_id, this);  // pass the movie ID to the fragment
        }
    }

    public static interface MovieAdapterOnClickHandler {
        void onClick(Long date, MovieViewHolder vh);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler dh, View emptyView) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mTM = TrafficManager.getInstance(mContext);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.list_item_movie;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new MovieViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    // set the contents in the card

    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int position) {
        mPosition = position;

        mCursor.moveToPosition(position);

//        String debugging_text = String.valueOf(position) + " PopIDX: "
//                + mCursor.getString(PopularMovieFragment.COL_POPULAR_INDEX) + " "
//                + mCursor.getString(PopularMovieFragment.COL_TITLE);
//        movieViewHolder.mDebuggingTextView.setText(debugging_text);

        String vote = mCursor.getString(PopularMovieFragment.COL_VOTE_AVERAGE);
        String movie_title = mCursor.getString(PopularMovieFragment.COL_TITLE);

        movieViewHolder.mDebuggingTextView.setText(vote);
        movieViewHolder.mDebuggingTextView.setContentDescription("Rated " + vote + " out of ten");

        movieViewHolder.mPosterView.setContentDescription(movie_title);
        String movie_poster_path = mCursor.getString(PopularMovieFragment.COL_POSTER_PATH);
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185/" + movie_poster_path)
                .error(R.drawable.ic_no_wifi)
                .placeholder(R.drawable.ic_loading)
                .into(movieViewHolder.mPosterView);

        // if we are in display favorites mode,
        // then adjust the colors of the background
        //    note adjusting the card color blows up on older versions!
        // only do this in tablet mode

        int movie_id = mCursor.getInt(PopularMovieFragment.COL_MOVIE_ID);
        movieViewHolder.mListItem.setBackgroundColor(
                mContext.getResources().getColor(R.color.white));

        String table_name = mCursor.getString(PopularMovieFragment.COL_THIS_TABLE_NAME);
        if (table_name.contains("favorites")) {
            int favorite_flag = mCursor.getInt(PopularMovieFragment.COL_FAVORITE);
            if (favorite_flag == 1) {
                movieViewHolder.mHeartFlagView.setVisibility(View.VISIBLE);
                movieViewHolder.mHeartFlagView.setContentDescription("The movie is a favorite");

                // this seems to mess up the layout!!  skip it.
//                if (android.os.Build.VERSION.SDK_INT >= 21) {
//                    movieViewHolder.mCardView.setElevation(
//                            mContext.getResources().getDimension(R.dimen.card_view_elevation)
//                    );
//                }
            } else {
                movieViewHolder.mHeartFlagView.setVisibility(View.INVISIBLE);
                Log.v(LOG_TAG, "**** out of favor!! Do something!");
//                    if (android.os.Build.VERSION.SDK_INT >= 21)
//                    {
//                        movieViewHolder.mCardView.setElevation(0);
//                    }
                movieViewHolder.mListItem.setBackgroundColor(
                        mContext.getResources().getColor(R.color.grey));
            }
        } else {
            int favorite_flag = mCursor.getInt(PopularMovieFragment.COL_FAVORITE);
            if (favorite_flag == 1 || mTM.checkFavoriteID(movie_id)) {
                movieViewHolder.mHeartFlagView.setVisibility(View.VISIBLE);
                movieViewHolder.mHeartFlagView.setContentDescription("The movie is a favorite");
            } else {
                movieViewHolder.mHeartFlagView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public void closeCursor() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof MovieViewHolder) {
            MovieViewHolder vfh = (MovieViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }
}