package com.example.playlist.Exceptions;

public class IncorrectRequestBodyExeption extends RuntimeException{
    public IncorrectRequestBodyExeption(String message)
    {
        super(message);
    }
}
