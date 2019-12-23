package com.example.mohamednadeem.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    handler currenthandler;
    private GridView mGridView;

    private MovieGridAdapter mMovieGridAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String Popular = "popular";
    private static final String Top_Rated = "top_rated";
    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mSortBy;

    private ArrayList<Movie> mMovies = null;



    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_IMAGE2 = 4;
    public static final int COL_OVERVIEW = 5;
    public static final int COL_RATING = 6;
    public static final int COL_DATE = 7;

    public MainActivityFragment() {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortby=sharedPref.getString("sortby",Popular);
        mSortBy=sortby;
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        if (mSortBy.contentEquals(Popular)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);

            }
        } else if (mSortBy.contentEquals(Top_Rated)) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);

            }
        } else if (mSortBy.contentEquals(FAVORITE)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_favorite.setChecked(true);

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = Popular;
                editor.putString("sortby",Popular);
                editor.commit();
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = Top_Rated;
                editor.putString("sortby",Top_Rated);
                editor.commit();
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = FAVORITE;
                editor.putString("sortby",FAVORITE);
                editor.commit();
                updateMovies(mSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridview_movies);

        mMovieGridAdapter = new MovieGridAdapter(getActivity(), new ArrayList<Movie>());

        mGridView.setAdapter(mMovieGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieGridAdapter.getItem(position);
            //    Intent intent=new Intent(getActivity(),DetailActivity.class)
          //              .putExtra("Movie",movie);
                currenthandler.setMovie(movie);
         //       startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mSortBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mMovieGridAdapter.setData(mMovies);
            } else {
                updateMovies(mSortBy);
            }
        } else {
            updateMovies(mSortBy);
        }

        return view;
    }
    public void setmovielistener(handler myhandler){
        currenthandler=myhandler;
    }

    private void updateMovies(String sort_by) {
        if (!sort_by.contentEquals(FAVORITE)) {
            new FetchMoviesTask().execute(sort_by);
        } else {
            List<String> FavoriteList = new ArrayList<String>();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Set<String> RetriveFavorite = sharedPref.getStringSet("FavoriteList", null);
            if(RetriveFavorite!=null)
                FavoriteList.addAll(RetriveFavorite);
            new FetchFavoriteMoviesTask().execute(FavoriteList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mSortBy.contentEquals(Popular)) {
            outState.putString(SORT_SETTING_KEY, mSortBy);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<Movie> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movie);
                results.add(movieModel);
            }

            return results;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                       // .appendQueryParameter(SORT_BY_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.tmdb_api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (mMovieGridAdapter != null) {
                    mMovieGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }

    public class FetchFavoriteMoviesTask extends AsyncTask<List<String>, Void, List<Movie>> {
        private final String LOG_TAG = FetchFavoriteMoviesTask.class.getSimpleName();
     private Movie getMoviesDataFromJson(String JsonStr)throws JSONException{
         JSONObject movieJson = new JSONObject(JsonStr);
         Movie movieModel = new Movie(movieJson);
         return movieModel;
     }
        @Override
        protected List<Movie> doInBackground(List<String>... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;
            List<Movie> Movies=new ArrayList<Movie>();
            for(int i=0;i<params[0].size();i++) {
                try {
                    final String BASE_URL = "http://api.themoviedb.org/3/movie";
                    final String API_KEY_PARAM = "api_key";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                               .appendPath(params[0].get(i))
                            .appendQueryParameter(API_KEY_PARAM, getString(R.string.tmdb_api_key))
                            .build();

                    URL url = new URL(builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        try {


                            urlConnection.disconnect();
                        }
                        catch (Exception e){
                            Log.e(LOG_TAG, "Error closing Connection", e);
                        }
                        }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    Movie movie=new Movie();
                    movie= getMoviesDataFromJson(jsonStr);
                    Movies.add(movie);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                /////////
            }
                return Movies;

            // This will only happen if there was an error getting or parsing the forecast.
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (mMovieGridAdapter != null) {
                    mMovieGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}