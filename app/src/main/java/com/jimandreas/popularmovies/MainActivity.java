package com.jimandreas.popularmovies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jimandreas.popularmovies.utils.Utility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopularMovieFragment.Callback {

    private final String LOG_TAG = TrafficManager.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static String DISPLAY_MODE = "displaymode";

    private boolean mTwoPane;
    private TrafficManager mTM = TrafficManager.getInstance(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTM.validateFavorites();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * note:   color gradient of nav_header_main is CAREFULLY HIDDEN
         * in drawable/side_nav_bar.xml
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
//            if (savedInstanceState != null) {
//                String display_mode = savedInstanceState.getString(DISPLAY_MODE);
//                setDisplayMode(display_mode);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

// TODO:  this has bugs when the movie detail fragment owns the screen
            // if the Favorites View is displayed,
            // don't exit straight out of the app, but return to the nav drawer
            // drop into Popular Movies view so that it will take two backs to
            // completely exit

//            String frag_mode = getDisplayMode();
//            if (frag_mode.contains(PopularMovieFragment.DISPLAY_FAVORITES)) {
//                drawer.openDrawer(GravityCompat.START);
//                setDisplayMode(PopularMovieFragment.DISPLAY_POPULAR);
//                setTitle(R.string.drawer_popular_movies);
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Boolean handled = true;

        if (id == R.id.popular_movies) {
            setDisplayMode(PopularMovieFragment.DISPLAY_POPULAR);
            setTitle(R.string.drawer_popular_movies);
            mTM.validateFavorites();
        } else if (id == R.id.highest_rated_movies) {
            setDisplayMode(PopularMovieFragment.DISPLAY_TOP_RATED);
            setTitle(R.string.drawer_top_rated);
            mTM.validateFavorites();
        } else if (id == R.id.favorite_movies) {
            if (mTM.numFavorites() > 0) {
                setDisplayMode(PopularMovieFragment.DISPLAY_FAVORITES);
                setTitle(R.string.drawer_favorites);
            } else {
                showNoFavoritesSetDialog();
                return false;
            }
        }  else if (id == R.id.nav_help) {
            Utility.viewUrl(getString(R.string.drawer_help_url), this);
        } else if (id == R.id.nav_about) {
            Utility.viewUrl(getString(R.string.drawer_info_url), this);
        }  else if (id == R.id.nav_whodunnit) {
            Utility.viewUrl(getString(R.string.drawer_whodunnit_url), this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return handled;
    }

    @Override
    public void onPause() {
        super.onPause();
        String display_mode = getDisplayMode();
        mTM.setDisplayMode(display_mode);
    }

    @Override
    public void onResume() {
        super.onResume();
        String display_mode = mTM.getDisplayMode();
        if (display_mode != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            if (display_mode.contains(PopularMovieFragment.DISPLAY_POPULAR)) {
                setTitle(R.string.drawer_popular_movies);
                navigationView.setCheckedItem(R.id.popular_movies);
            } else if (display_mode.contains(PopularMovieFragment.DISPLAY_TOP_RATED)) {
                setTitle(R.string.drawer_top_rated);
                navigationView.setCheckedItem(R.id.highest_rated_movies);
            } else if (display_mode.contains(PopularMovieFragment.DISPLAY_FAVORITES)) {
                setTitle(R.string.drawer_favorites);
                navigationView.setCheckedItem(R.id.favorite_movies);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String display_mode = getDisplayMode();
        outState.putString(DISPLAY_MODE, display_mode);
        super.onSaveInstanceState(outState);
    }

    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent detailsIntent = new Intent(this, MovieDetailActivity.class)
                    .setData(contentUri);
            startActivity(detailsIntent);
        }
    }

    public void setDisplayMode(String mode) {
        PopularMovieFragment frag = (PopularMovieFragment)
                getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
        if (frag != null) {
            frag.updateDisplayMode(mode);
        }
    }


    public String getDisplayMode() {
        PopularMovieFragment frag = (PopularMovieFragment)
                getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
        if (frag != null) {
            return( frag.getDisplayMode());
        }
        return null;
    }

    /*
     * with acknowledgements to:
     * http://stackoverflow.com/a/14134437/3853712
     */
    public void showNoFavoritesSetDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Favorites");
        builder1.setIcon(R.drawable.ic_favorite_selected);
        builder1.setMessage(R.string.dialog_no_favorites_set);
        builder1.setCancelable(true);
        builder1.setNeutralButton(R.string.dialog_no_favorites_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
}
