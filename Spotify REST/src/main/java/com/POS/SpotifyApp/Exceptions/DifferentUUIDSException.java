package com.POS.SpotifyApp.Exceptions;

public class DifferentUUIDSException extends RuntimeException{
    public DifferentUUIDSException()
    {
        super("UUIDs are not the same - the one in the body and the one in the path");
    }
}
