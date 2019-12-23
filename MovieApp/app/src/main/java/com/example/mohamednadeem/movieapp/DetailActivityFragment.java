package com.example.mohamednadeem.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    String[] MovieTrailer_Name={};
    Boolean isFavorite;
    ListView lv;
    ListView lv1;
   // DetailActivity context=getActivity();
    Movie mymovie=new Movie();
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private boolean Checkmovieisfavorite(){
        List<String> FavoriteList = new ArrayList<String>();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> RetriveFavorite = sharedPref.getStringSet("FavoriteList", null);
        if(RetriveFavorite!=null)
            FavoriteList.addAll(RetriveFavorite);
        for(int i=0;i<FavoriteList.size();i++){
            int currentid=Integer.parseInt(FavoriteList.get(i));
            if(mymovie.getId()==currentid)
                return true;

        }
        return false;
    }
    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onPause() {
        super.onPause();
        fetchMovieReview.cancel(true);
        fetchMovieTrailer.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getArguments();
        mymovie = bundle.getParcelable("Movie");

        updateTrailer();
        updateReview();

        isFavorite=Checkmovieisfavorite();
        final Button FavoriteButton =(Button)  view.findViewById(R.id.Favorite);
        if(isFavorite){
            FavoriteButton.setText(R.string.removed_from_favorites);
            Drawable img = getResources().getDrawable(R.drawable.favorite);
            FavoriteButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        else{
            FavoriteButton.setText(R.string.added_to_favorites);
            Drawable img = getResources().getDrawable(R.drawable.notfavorite);
            FavoriteButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        TextView MovieName=(TextView)   view.findViewById(R.id.Movie_Name);
        TextView MovieDescription=(TextView)   view.findViewById(R.id.Movie_Description);
        TextView MovieDate=(TextView)  view.findViewById(R.id.Movie_date);
        TextView MoviewAverageRate=(TextView)  view.findViewById(R.id.Average_Rate);
        ImageView MoviePoster=(ImageView)  view.findViewById(R.id.Movie_Poster);

        String image_url = "http://image.tmdb.org/t/p/w92" + mymovie.getImage();
        Glide.with(this).load(image_url).into(MoviePoster);
        MovieName.setText(mymovie.getTitle());
        MovieDescription.setText(mymovie.getOverview());
        MovieDate.setText("Release Date: "+mymovie.getDate());
        MoviewAverageRate.setText("Rate: "+Integer.toString(mymovie.getRating())+"/10");
        FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite==false) {
                    List<String> FavoriteList = new ArrayList<String>();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    SharedPreferences.Editor editor = sharedPref.edit();
                    Set<String> RetriveFavorite = sharedPref.getStringSet("FavoriteList", null);
                    if(RetriveFavorite!=null)
                        FavoriteList.addAll(RetriveFavorite);
                    FavoriteList.add(Integer.toString(mymovie.getId()));
                    Set<String> StoreFavorite = new HashSet<String>();
                    StoreFavorite.addAll(FavoriteList);
                    editor.putStringSet("FavoriteList", StoreFavorite);
                    editor.commit();
                    FavoriteButton.setText(R.string.removed_from_favorites);
                    Drawable img = getResources().getDrawable(R.drawable.favorite);
                    FavoriteButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
                else{
                    List<String> FavoriteList = new ArrayList<String>();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    SharedPreferences.Editor editor = sharedPref.edit();
                    Set<String> RetriveFavorite = sharedPref.getStringSet("FavoriteList", null);
                    if(RetriveFavorite!=null)
                        FavoriteList.addAll(RetriveFavorite);
                    FavoriteList.remove(Integer.toString(mymovie.getId()));
                    Set<String> StoreFavorite = new HashSet<String>();
                    StoreFavorite.addAll(FavoriteList);
                    editor.putStringSet("FavoriteList", StoreFavorite);
                    editor.commit();
                    FavoriteButton.setText(R.string.added_to_favorites);
                    Drawable img = getResources().getDrawable(R.drawable.notfavorite);
                    FavoriteButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
            }
        });

        reviewAdapter=new ReviewAdapter(getActivity(),MovieTrailer_Name,MovieTrailer_Name);
        lv1=(ListView)  view.findViewById(R.id.Review_List);
        lv1.setAdapter(reviewAdapter);
        trailerAdapter=new TrailerAdapter( getActivity(),MovieTrailer_Name);
        lv=(ListView) view.findViewById(R.id.Trailer_List);
        lv.setAdapter(trailerAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  Movie movie = mMovieGridAdapter.getItem(position);
                String key=mymovie.trailers.get(position).key;
                String url="https://www.youtube.com/watch?";
                String video_param="v";
                Uri builtUri = Uri.parse(url).buildUpon()
                        .appendQueryParameter(video_param,key)
                        .build();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, builtUri);
                startActivity(browserIntent);
                //Intent intent=new Intent(getActivity(),DetailActivity.class)
                //      .putExtra("Movie",movie);

                //startActivity(intent);
            }
        });



        return view;
    }



    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    FetchMovieTrailer fetchMovieTrailer=new FetchMovieTrailer();
    private void updateTrailer(){
        //FetchMovieTrailer fetchMovieTrailer=new FetchMovieTrailer();
        fetchMovieTrailer.execute(mymovie);
    }
    FetchMovieReview fetchMovieReview=new FetchMovieReview();
    private void updateReview(){

        fetchMovieReview.execute(mymovie);
    }
    public class FetchMovieTrailer extends AsyncTask<Movie,Void,ArrayList<MovieTrailer>> {
        private final String Log_Tag = FetchMovieTrailer.class.getSimpleName();

        @Override
        protected ArrayList<MovieTrailer> doInBackground(Movie... Movie) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonTrailer = null;

            try {
                final String Forecast_Base_Url = "http://api.themoviedb.org/3/movie/";
                Uri builtUri = Uri.parse(Forecast_Base_Url).buildUpon()
                        .appendPath(Integer.toString( Movie[0].getId()))
                        .appendPath("videos")
                        .appendQueryParameter("api_key", getString(R.string.tmdb_api_key))
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(Log_Tag, "Built URI " + builtUri.toString());
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
                    if(isCancelled())
                        break;

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                MovieJsonTrailer = buffer.toString();
            } catch (IOException e) {
                Log.e(Log_Tag, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(Log_Tag, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieTrailerDataFromJson(MovieJsonTrailer,Movie[0]);
            } catch (Exception e) {
                Log.e(Log_Tag, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        private ArrayList<MovieTrailer> getMovieTrailerDataFromJson(String MovieTrailerJsonStr,Movie movie)throws JSONException {
            JSONObject MovieJson=new JSONObject(MovieTrailerJsonStr);
            JSONArray MovieTrailers=MovieJson.getJSONArray("results");
            movie.trailers=new ArrayList<MovieTrailer>();

            for(int i=0;i<MovieTrailers.length();i++){
                if(isCancelled())
                    break;
                MovieTrailer currenttrailer=new MovieTrailer();
                JSONObject moviet = MovieTrailers.getJSONObject(i);
                currenttrailer.key=moviet.getString("key");
                currenttrailer.name=moviet.getString("name");
                currenttrailer.site=moviet.getString("site");
                currenttrailer.type=moviet.getString("type");
                movie.trailers.add(currenttrailer);
            }
            return movie.trailers;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieTrailer> movieTrailers) {
            ArrayList<String> TrailerName=new ArrayList<String>();
            for(int i=0;i<movieTrailers.size();i++){

                TrailerName.add(movieTrailers.get(i).name);


            }
            String[]trailer=new String[TrailerName.size()];
            TrailerName.toArray(trailer);
            trailerAdapter=  new TrailerAdapter(getActivity(),trailer);
            lv.setAdapter(trailerAdapter);
            setListViewHeightBasedOnChildren(lv);
        }

    }

    public class FetchMovieReview extends AsyncTask<Movie,Void,ArrayList<MovieReview>> {
        private final String Log_Tag = FetchMovieReview.class.getSimpleName();

        @Override
        protected ArrayList<MovieReview> doInBackground(Movie... Movie) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonTrailer = null;

            try {
                final String Forecast_Base_Url = "http://api.themoviedb.org/3/movie/";
                Uri builtUri = Uri.parse(Forecast_Base_Url).buildUpon()
                        .appendPath(Integer.toString( Movie[0].getId()))
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", getString(R.string.tmdb_api_key))
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(Log_Tag, "Built URI " + builtUri.toString());
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
                    if(isCancelled())
                        break;
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                MovieJsonTrailer = buffer.toString();
            } catch (IOException e) {
                Log.e(Log_Tag, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(Log_Tag, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieReviewDataFromJson(MovieJsonTrailer,Movie[0]);
            } catch (Exception e) {
                Log.e(Log_Tag, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        private ArrayList<MovieReview> getMovieReviewDataFromJson(String MovieReviewJsonStr,Movie movie)throws JSONException{
            JSONObject MovieJson=new JSONObject(MovieReviewJsonStr);
            JSONArray MovieReviews=MovieJson.getJSONArray("results");
            movie.reviews=new ArrayList<MovieReview>();

            for(int i=0;i<MovieReviews.length();i++){
                if(isCancelled())
                    break;
                MovieReview currentreview=new MovieReview();
                JSONObject moviet = MovieReviews.getJSONObject(i);
                currentreview.author=moviet.getString("author");
                currentreview.content=moviet.getString("content");
                movie.reviews.add(currentreview);
            }
            return movie.reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieReview> moviereview) {
            ArrayList<String> review_author=new ArrayList<String>();
            ArrayList<String> review_content=new ArrayList<String>();
            for(int i=0;i<moviereview.size();i++){

                review_author.add(moviereview.get(i).author);
                review_content.add(moviereview.get(i).content);

            }
            String[]review_authors=new String[review_author.size()];
            String[]review_contents=new String[review_content.size()];
            review_author.toArray(review_authors);
            review_content.toArray(review_contents);
            reviewAdapter=  new ReviewAdapter(getActivity(),review_authors,review_contents);
            lv1.setAdapter(reviewAdapter);
            setListViewHeightBasedOnChildren(lv1);
        }

    }





}
