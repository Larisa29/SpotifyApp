package com.example.playlist.View;

import com.example.playlist.DataAccess.Models.SongDetails;
import com.example.playlist.Enums.Visibility;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class PlaylistDTO {
    @Id
    private String id;
    private Integer userId;
    private String playlistName;
    private String visibility;
    private List<SongDetails> songs = new ArrayList<>();

    public PlaylistDTO() {}
    public PlaylistDTO(Integer userId, String playlistName, List<SongDetails> songs)
    {
        this.userId = userId;
        this.playlistName = playlistName;
        this.songs = songs;
    }
}