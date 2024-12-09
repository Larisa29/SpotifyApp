package com.POS.SpotifyApp.Exceptions;

public class ArtistDbIsEmptyException extends RuntimeException{
    public ArtistDbIsEmptyException()
    {
        super("There is no artist that matches your filters in the system.");
    }
}
