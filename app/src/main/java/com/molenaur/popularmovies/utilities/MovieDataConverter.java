package com.molenaur.popularmovies.utilities;

import com.molenaur.popularmovies.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmolenaur on 7/9/2017.
 */

public class MovieDataConverter {
    public static boolean canRequestMoreData(String resultsJSONString) {
        boolean canRequestMoreData = true;
        try {
            JSONObject results = new JSONObject(resultsJSONString);
            int page = results.getInt("page");
            int totalPages = results.getInt("total_pages");
            canRequestMoreData = page < totalPages;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return canRequestMoreData;
    }

    public static List<MovieData> getMovieData(String resultsJSONString) {
        List<MovieData> movieData = new ArrayList<>();
        try {
            JSONObject results = new JSONObject(resultsJSONString);
            JSONArray movies = new JSONArray(results.getString("results"));
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);
                String posterPath = movie.getString("poster_path");
                double voteAverage = movie.getDouble("vote_average");
                String title = movie.getString("title");
                String overview = movie.getString("overview");
                String releaseDate = movie.getString("release_date");
                movieData.add(new MovieData(posterPath, voteAverage, title, overview, releaseDate));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieData;
    }
}
