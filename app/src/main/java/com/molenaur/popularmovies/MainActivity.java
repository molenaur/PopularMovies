package com.molenaur.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.molenaur.popularmovies.utilities.MovieDataConverter;
import com.molenaur.popularmovies.utilities.NetworkUtils;
import com.molenaur.popularmovies.utilities.TheMovieDatabaseUtils;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_SORT_BY_POPULAR = "sort_by_popular";
    private static final String KEY_MOVIE_DATA = "movie_data";
    private static final String KEY_PAGE = "page";
    private static final String KEY_CAN_REQUEST_MORE_DATA = "can_request_more_data";
    private RecyclerView rvPosters;
    private ProgressBar loadingIndicator;
    private TextView errorText;
    private boolean isSortByPopular;
    private int page;
    private boolean canRequestMoreData;
    private boolean isRequestingData;
    private MovieDatabaseTask movieDatabaseTask;
    private PosterAdapter.PosterItemClickListener posterItemClickListener;
    private List<MovieData> movieData;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPosters = (RecyclerView) findViewById(R.id.rv_posters);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        errorText = (TextView) findViewById(R.id.tv_error);

        setupPostersView();

        posterItemClickListener = new PosterAdapter.PosterItemClickListener() {
            @Override
            public void onPosterItemClick(int clickItemIndex) {
                Intent posterIntent = new Intent(MainActivity.this, MovieActivity.class);
                posterIntent.putExtra(MovieActivity.MOVIE_DATA, movieData.get(clickItemIndex));
                startActivity(posterIntent);
            }
        };

        if (savedInstanceState != null) {
            isSortByPopular = savedInstanceState.getBoolean(KEY_SORT_BY_POPULAR);
            movieData = (List<MovieData>)savedInstanceState.getSerializable(KEY_MOVIE_DATA);
            PosterAdapter posterAdapter = new PosterAdapter(this, getPosterData(movieData), posterItemClickListener);
            rvPosters.setAdapter(posterAdapter);
            page = savedInstanceState.getInt(KEY_PAGE);
            canRequestMoreData = savedInstanceState.getBoolean(KEY_CAN_REQUEST_MORE_DATA);
            isRequestingData = false;
        } else {
            resetData();
            isSortByPopular = true;
            requestMovieData();
        }
        setTitle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movieData != null && !movieData.isEmpty()) {
            outState.putBoolean(KEY_SORT_BY_POPULAR, isSortByPopular);
            outState.putSerializable(KEY_MOVIE_DATA, (Serializable) movieData);
            outState.putInt(KEY_PAGE, page);
            outState.putBoolean(KEY_CAN_REQUEST_MORE_DATA, canRequestMoreData);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (movieDatabaseTask != null) {
            movieDatabaseTask.cancel(true);
        }
    }

    private void setupPostersView() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            rvPosters.setLayoutManager(gridLayoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rvPosters.setLayoutManager(gridLayoutManager);
        }
        rvPosters.setHasFixedSize(true);
        rvPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (canRequestMoreData && !isRequestingData && (dx > 0 || dy > 0)) {
                    int totalItems = recyclerView.getLayoutManager().getItemCount();
                    int lastVisibleItem = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (totalItems - lastVisibleItem < 15) {
                        // load the next page of data ahead of time so it's ready by the time we scroll there
                        requestMovieData();
                    }
                }
            }
        });
    }

    private List<PosterData> getPosterData(List<MovieData> movies) {
        List<PosterData> posters = new ArrayList<>();
        for (MovieData movie : movies) {
            posters.add(new PosterData(movie.getPosterPath(), movie.getTitle()));
        }
        return posters;
    }

    private void resetData() {
        if (movieDatabaseTask != null) {
            movieDatabaseTask.cancel(true);
        }
        movieData = new ArrayList<>();
        PosterAdapter posterAdapter = new PosterAdapter(this, new ArrayList<PosterData>(), posterItemClickListener);
        rvPosters.setAdapter(posterAdapter);
        page = 1;
        canRequestMoreData = true;
        isRequestingData = false;
    }

    private void requestMovieData() {
        if (canRequestMoreData) {
            URL movieUrl = isSortByPopular ? TheMovieDatabaseUtils.getPopularMoviesUrl(page) : TheMovieDatabaseUtils.getTopRatedMoviesUrl(page);
            movieDatabaseTask = (MovieDatabaseTask) new MovieDatabaseTask().execute(movieUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // set menu item check so it's displayed correctly after rotation
        int menuItemResId = isSortByPopular ? R.id.action_popular : R.id.action_top_rated;
        MenuItem item = menu.findItem(menuItemResId);
        if (item != null) {
            item.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedId = item.getItemId();
        switch (clickedId) {
            case R.id.action_popular:
                if (!isSortByPopular) {
                    isSortByPopular = true;
                    item.setChecked(true);
                    resetData();
                    setTitle();
                    requestMovieData();
                }
                return true;
            case R.id.action_top_rated:
                if (isSortByPopular) {
                    isSortByPopular = false;
                    item.setChecked(true);
                    resetData();
                    setTitle();
                    requestMovieData();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTitle() {
        if (getSupportActionBar() != null) {
            if (isSortByPopular) {
                getSupportActionBar().setTitle(R.string.popular_movies);
            } else {
                getSupportActionBar().setTitle(R.string.top_rated_movies);
            }
        }
    }

    private void showProgressBar() {
        loadingIndicator.setVisibility(View.VISIBLE);
        rvPosters.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void showPosters() {
        rvPosters.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showError(String errorMessage) {
        errorText.setText(errorMessage);
        errorText.setVisibility(View.VISIBLE);
        rvPosters.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    private class MovieDatabaseTask extends AsyncTask<URL, Void, String> {
        private static final String ERROR = "ERROR";

        @Override
        protected String doInBackground(URL... urls) {
            try {
                URL movieUrl = urls[0];
                return NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (Exception e) {
                if (NetworkUtils.canConnectToInternet(MainActivity.this)) {
                    return ERROR + MainActivity.this.getResources().getString(R.string.loading_error);
                } else {
                    return ERROR + MainActivity.this.getResources().getString(R.string.no_internet_error);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            isRequestingData = true;
            if (movieData == null || movieData.isEmpty()) {
                showProgressBar();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null || s.isEmpty()) {
                // no results
                showError(MainActivity.this.getResources().getString(R.string.no_results_error));
            } else if (s.startsWith(ERROR)) {
                // no network connection or loading error
                showError(s.substring(ERROR.length()));
            } else {
                // success
                canRequestMoreData = MovieDataConverter.canRequestMoreData(s);
                List<MovieData> movies = MovieDataConverter.getMovieData(s);
                if (movieData == null) {
                    movieData = new ArrayList<>();
                }
                movieData.addAll(movies);
                ((PosterAdapter) rvPosters.getAdapter()).addPosterData(getPosterData(movies));
                showPosters();
                isRequestingData = false;
                page++;
            }
        }
    }
}
