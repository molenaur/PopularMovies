package com.molenaur.popularmovies.utilities;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class TheMovieDatabaseUtils {
    private final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String POPULAR_PATH = "popular";
    private final static String TOP_RATED_PATH = "top_rated";
    private final static String PARAM_API_KEY = "api_key";
    // You must supply your own api key here for The Movie Database
    private final static String API_KEY = "";
    private final static String PARAM_PAGE = "page";

    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";

    public static URL getPopularMoviesUrl(int page) {
        return getUrlWithPath(POPULAR_PATH, page);
    }

    public static URL getTopRatedMoviesUrl(int page) {
        return getUrlWithPath(TOP_RATED_PATH, page);
    }

    private static URL getUrlWithPath(String path, int page) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        return getUrlFromUri(builtUri);
    }

    public static URL getMoviePosterUrl(String posterPath) {
        Uri posterUri = Uri.parse(IMAGE_BASE_URL + posterPath);
        return getUrlFromUri(posterUri);
    }

    private static URL getUrlFromUri(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
