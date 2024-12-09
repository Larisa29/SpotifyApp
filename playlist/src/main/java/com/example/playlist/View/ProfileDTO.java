package com.example.playlist.View;

import com.example.playlist.DataAccess.Models.Playlist;
import lombok.Data;

import java.util.*;

@Data
public class ProfileDTO {
    private Integer userId;
    private String userName;
    private String email;
    private List<SongDTO> likedMusic = new ArrayList<>();
    private Set<Playlist> playlists = new HashSet<>();

    public ProfileDTO(Integer userId, String username, String useremail)
    {
        this.userId = userId;
        this.userName = username;
        this.email = useremail;
    }

    //public ProfileDTO(){};
    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }
}
