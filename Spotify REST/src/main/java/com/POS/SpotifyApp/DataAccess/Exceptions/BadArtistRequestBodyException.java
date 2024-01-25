package com.POS.SpotifyApp.DataAccess.Exceptions;

public class BadArtistRequestBodyException extends RuntimeException{
    public BadArtistRequestBodyException()
    {
        super("Bad Request. Artist info fields cannot be null.");
    }
}
