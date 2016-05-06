package haris.moviesdb;

/**
 * Created by haris on 04-May-16.
 */
public class Movies {
    String title;
    String poster;
    String synopsis; // (called overview in the api)
    String rating; //(called vote_average in the api)
    String release_date;

    public Movies(String title, String poster, String synopsis, String rating, String release_date){
        this.title = title;
        this.poster = poster;
        this. synopsis = synopsis;
        this. rating = rating;
        this.release_date = release_date;
    }
}