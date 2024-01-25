package com.POS.SpotifyApp.DataAccess.Exceptions;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(int id)
    {
        super("Resource identified by id= " + id + " NOT FOUND");
    }
}
