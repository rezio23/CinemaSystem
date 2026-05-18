package com.cinema.model;

public class Movie {
    private int movieId;
    private String title;
    private String genre;
    private String director;
    private int durationMin;
    private String rating;
    private int releaseYear;

    public Movie() {}

    public Movie(int movieId, String title, String genre, String director, int durationMin, String rating, int releaseYear) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.durationMin = durationMin;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    @Override
    public String toString() {
        return title + " (" + releaseYear + ")";
    }
}
