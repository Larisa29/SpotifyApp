package com.example.playlist.DataAccess.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "users")
public class Profile {

    @Id
    private String id;
    private Integer userId;
    private String userName;
    private List<SongDetails> likedMusic = new ArrayList<>();
    private Set<Playlist> playlists = new HashSet<>();

    public Profile(Integer userId, String username)
    {
        this.userId = userId;
        this.userName = username;
    }

    public Profile(){};
    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }




}
