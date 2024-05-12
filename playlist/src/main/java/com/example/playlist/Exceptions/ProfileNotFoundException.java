package com.example.playlist.Exceptions;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(int id)
    {
        super("Profile identified by id= " + id + " NOT FOUND");
    }
}
