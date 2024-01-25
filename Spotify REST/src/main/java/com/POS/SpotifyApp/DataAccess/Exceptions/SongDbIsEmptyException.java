package com.POS.SpotifyApp.DataAccess.Exceptions;

public class SongDbIsEmptyException extends RuntimeException{
    public SongDbIsEmptyException()
    {
        super("There is no music in the system.");
    }
}
