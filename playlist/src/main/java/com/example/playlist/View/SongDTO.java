package com.example.playlist.View;
import lombok.Data;

@Data
public class SongDTO {
    private Integer id;
    private String name;
    private String selflink;

    public SongDTO(Integer id, String name, String selflink) {
        this.id = id;
        this.name = name;
        this.selflink = selflink;
    }
    public SongDTO(){};

}

