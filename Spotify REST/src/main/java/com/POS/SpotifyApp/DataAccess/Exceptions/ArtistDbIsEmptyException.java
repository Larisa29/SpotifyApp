package com.POS.SpotifyApp.DataAccess.Exceptions;

public class ArtistDbIsEmptyException extends RuntimeException{
    public ArtistDbIsEmptyException()
    {
        super("There is no artist in the system.");
    }
}
