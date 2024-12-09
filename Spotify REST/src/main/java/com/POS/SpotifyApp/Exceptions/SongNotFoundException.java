package com.POS.SpotifyApp.Exceptions;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(int id)
    {
        super("Resource identified by id= " + id + " NOT FOUND");
    }
}
