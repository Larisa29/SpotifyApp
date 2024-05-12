package com.example.playlist.Exceptions;

public class PlaylistNameAlreadyExistsException extends RuntimeException{
    public PlaylistNameAlreadyExistsException(String playlistName)
    {
        super("Current user has already created a playlist with this name: " + playlistName);
    }
}
