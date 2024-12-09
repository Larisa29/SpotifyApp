package com.POS.SpotifyApp.Exceptions;

public class SongDbIsEmptyException extends RuntimeException{
    public SongDbIsEmptyException()
    {
        super("There is no song that matches your filters in the system.");
    }
}
