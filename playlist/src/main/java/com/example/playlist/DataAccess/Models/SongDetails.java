package com.example.playlist.DataAccess.Models;

import lombok.Data;

@Data
public class SongDetails {
    private Integer id;
    private String name;
    private String selflink;

    public SongDetails(Integer id, String name, String selflink) {
        this.id = id;
        this.name = name;
        this.selflink = selflink;
    }
    public SongDetails(){};

}
