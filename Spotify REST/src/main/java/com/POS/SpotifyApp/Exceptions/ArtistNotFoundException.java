package com.POS.SpotifyApp.Exceptions;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String id)
    {
        super("Artist identified by id= " + id + " NOT FOUND");
    }
}