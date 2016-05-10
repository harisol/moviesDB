package haris.moviesdb;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;

/**
 * Created by haris on 06-May-16.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter ;
    // type of movies, most popular or top rated. Default is most popular
    public String sort_type = "popular";

    // set the movie items
    Movies[] movies = {
            new Movies("judul", "https://image.tmdb.org/t/p/w185/saF3HtAduvrP9ytXDxSnQJP3oqx.jpg", "sinop", "rating", "rilis"),
            new Movies("judul", "https://image.tmdb.org/t/p/w185/xiosOeLfzPbfLfqui41kSWnO0sZ.jpg", "sinop", "rating", "rilis"),
            new Movies("judul", "https://image.tmdb.org/t/p/w185/5N20rQURev5CNDcMjHVUZhpoCNC.jpg", "sinop", "rating", "rilis"),
            new Movies("judul", "https://image.tmdb.org/t/p/w185/6bCplVkhowCjTHXWv49UjRPn0eK.jpg", "sinop", "rating", "rilis"),
            new Movies("judul", "https://image.tmdb.org/t/p/w185/vOipe2myi26UDwP978hsYOrnUWC.jpg", "sinop", "rating", "rilis")
    };

    public MainActivityFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.popular){
            this.sort_type = "popular";
            updateMovies(this.sort_type);
            return true;
        }else if (id == R.id.top){
            this.sort_type = "top";
            updateMovies(this.sort_type);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Movies> movieList = new ArrayList<Movies>(Arrays.asList(movies));
        movieAdapter = new MovieAdapter(getActivity(), movieList);

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview);
        gridview.setAdapter(movieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Movies movieClick = movieAdapter.getItem(position);
                String[] movieBundle = {
                        movieClick.title,
                        movieClick.poster,
                        movieClick.release_date,
                        movieClick.rating,
                        movieClick.synopsis
                };
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieBundle);
                startActivity(intent);
//                movieAdapter.notifyDataSetChanged();
            }
        });

        return rootview;
    }

    private void updateMovies(String sort_type) {
        movieAdapter.clear();
        FetchMovieTask movieTask = new FetchMovieTask(sort_type);
        Movies a = new Movies();
        movieTask.execute(a);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(this.sort_type);
    }

    public class FetchMovieTask extends AsyncTask<Movies, Void, Movies[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private String sort_type;

        FetchMovieTask(String sort_type){
            this.sort_type = sort_type;
        }

        // fix the image url format
        private String formatImageUrl(String poster_path) {
            String base_url = "http://image.tmdb.org/t/p/";
            String image_size = "w185";
            String image_url = base_url + image_size + poster_path;

            return image_url;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movies[] getMovieDataFromJson(String movieJsonStr, int numMovies)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MD_list = "results";
            final String MD_title = "original_title";
            final String MD_poster = "poster_path";
            final String MD_synopsis = "overview";
            final String MD_rating = "vote_average";
            final String MD_release = "release_date";

            // convert json string (mentah) menjadi json object
            JSONObject movieJson = new JSONObject(movieJsonStr);
            // ambil json array yang bernama 'results'
            JSONArray movieArray = movieJson.getJSONArray(MD_list);

            // Untuk hasil parsing json fixnya
            Movies[] resultMovies = new Movies[numMovies];
            for(int i = 0; i < movieArray.length(); i++) {
                String title;
                String poster;
                String synopsis;
                String rating;
                String release;

                // Get the JSON object representing the movie (array ke i)
                JSONObject movieDetail = movieArray.getJSONObject(i);

                title = movieDetail.getString(MD_title);
                poster = movieDetail.getString(MD_poster);
                poster = formatImageUrl(poster);
                synopsis = movieDetail.getString(MD_synopsis);
                rating = movieDetail.getString(MD_rating);
                release = movieDetail.getString(MD_release);

                resultMovies[i] = new Movies(title, poster, synopsis, rating, release);
//                resultMovie[i] = title + "\n" + poster + "\n" + synopsis + "\n" + rating + "\n" + release;
            }

//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Movie Detail: " + s);
//            }
            return resultMovies;

        }

        @Override
        protected Movies[] doInBackground(Movies... params){
            // ------- PROSES HTTP REQUEST -------

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            // MASIH HARDCODE, BENERIN COBA!
            int numMovies = 20;
            try {
                // URI Parameter
//                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/popular?xml&api_key=5e5ddf7981bfbd0d2a3ea3943f106f8e";
//                final String QUERY_PARAM = "zip"; // cityname (q), city id (id), zip code (zip), etc
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";
//                final String APPID_PARAM = "APPID";

                // URI builder
//                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
//                        .build();
                URL url;
                if (this.sort_type == "popular"){
                    url = new URL("http://api.themoviedb.org/3/movie/popular?xml&api_key=5e5ddf7981bfbd0d2a3ea3943f106f8e");
                }else{
                    url = new URL("http://api.themoviedb.org/3/movie/top_rated?xml&api_key=5e5ddf7981bfbd0d2a3ea3943f106f8e");
                }

                Log.v(LOG_TAG, "Built URI " + url.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Movie JSON String: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
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
            // ------- END PROSES HTTP REQUEST -------

            // Parsing json
            try{
                return getMovieDataFromJson(movieJsonStr, numMovies);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movies[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(Movies movies : result) {
                    movieAdapter.add(movies);
                }
                // New data is back from the server.
            }
        }
    }



}
