package com.molenaur.popularmovies;

import java.io.Serializable;

/**
 * Created by jmolenaur on 7/9/2017.
 */

public class MovieData implements Serializable {
    private String posterPath;
    private double voteAverage;
    private String title;
    private String overview;
    private String releaseDate;

    public MovieData(String posterPath, double voteAverage, String title, String overview, String releaseDate) {
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
