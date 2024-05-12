package com.example.playlist.DataAccess.Models;

import com.example.playlist.View.SongDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;
    private Integer userId;
    private String userName;
    private String email;
    private List<SongDTO> likedMusic = new ArrayList<>();
    private Set<Playlist> playlists = new HashSet<>();

    public Profile(Integer userId, String username, String email)
    {
        this.userId = userId;
        this.userName = username;
        this.email = email;
    }

    public Profile(){};
    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }




}
