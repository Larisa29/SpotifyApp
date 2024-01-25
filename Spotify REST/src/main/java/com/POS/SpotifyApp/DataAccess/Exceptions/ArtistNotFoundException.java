package com.POS.SpotifyApp.DataAccess.Exceptions;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String id)
    {
        super("Artist identified by id= " + id + " NOT FOUND");
    }
}