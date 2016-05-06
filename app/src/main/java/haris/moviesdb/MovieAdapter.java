package haris.moviesdb;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by haris on 03-May-16.
 */
public class MovieAdapter extends ArrayAdapter<Movies> {
    //untuk log
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    // constructor
    public MovieAdapter(Activity context, List<Movies> movies){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.

        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    public View getView(int position, View convertView, ViewGroup parent){
        Movies movies = getItem(position);
        Context mContext = getContext();

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.movies_item, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById(R.id.poster);
        Picasso.with(mContext)
                .load(movies.poster)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(poster);

        return convertView;
    }
}
