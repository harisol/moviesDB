package haris.moviesdb;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

/**
 * Created by haris on 06-May-16.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter ;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movies));

        GridView gridview = (GridView) rootview.findViewById(R.id.gridview);
        gridview.setAdapter(movieAdapter);

        return rootview;
    }

}
