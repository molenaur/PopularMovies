package com.molenaur.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.molenaur.popularmovies.utilities.TheMovieDatabaseUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jmolenaur on 8/27/2017.
 */

public class MovieActivity extends AppCompatActivity {
    public static final String MOVIE_DATA = "MOVIE_DATA";
    private MovieData movieData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MOVIE_DATA)) {
            movieData = (MovieData) intent.getSerializableExtra(MOVIE_DATA);
        }

        ImageView poster = (ImageView) findViewById(R.id.iv_poster);
        URL posterUrl = TheMovieDatabaseUtils.getMoviePosterUrl(movieData.getPosterPath());
        Picasso.with(this).load(posterUrl.toString()).into(poster);
        TextView title = (TextView) findViewById(R.id.tv_movie_title);
        title.setText(movieData.getTitle());
        TextView overview = (TextView) findViewById(R.id.tv_overview);
        overview.setText(movieData.getOverview());
        TextView releaseDate = (TextView) findViewById(R.id.tv_release_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = dateFormat.parse(movieData.getReleaseDate());
            dateFormat.applyPattern("yyyy");
            releaseDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            // just set the release date to whatever string was passed
            releaseDate.setText(movieData.getReleaseDate());
        }
        TextView voteAverage = (TextView) findViewById(R.id.tv_vote_average);
        String vote = String.format(Locale.US, "%.1f/10", movieData.getVoteAverage());
        voteAverage.setText(vote);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
