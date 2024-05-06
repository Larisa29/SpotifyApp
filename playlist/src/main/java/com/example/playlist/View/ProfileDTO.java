package com.example.playlist.View;

import com.example.playlist.DataAccess.Models.Playlist;
import com.example.playlist.DataAccess.Models.SongDetails;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ProfileDTO {
    @Id
    private String id;
    private Integer userId;
    private String userName;
    private List<SongDetails> likedMusic = new ArrayList<>();
    private Set<Playlist> playlists = new HashSet<>();

    public ProfileDTO(Integer userId, String username)
    {
        this.userId = userId;
        this.userName = username;
    }

    public ProfileDTO(){};
    public void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }
}
