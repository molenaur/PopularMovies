package com.molenaur.popularmovies;

/**
 * Created by jmolenaur on 8/29/2017.
 */

public class PosterData {
    private String posterPath;
    private String title;

    public PosterData(String posterPath, String title) {
        this.posterPath = posterPath;
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }
}
