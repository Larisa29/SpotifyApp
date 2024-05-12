package com.example.playlist.Exceptions;

public class DuplicateProfileException extends RuntimeException{
    public DuplicateProfileException(int id)
    {
        super("Duplicate profile for user with id:  " + id);
    }
}
