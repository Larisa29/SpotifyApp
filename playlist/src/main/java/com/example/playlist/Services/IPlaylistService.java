package com.example.playlist.Services;

import com.example.playlist.DataAccess.Models.Playlist;
import com.example.playlist.View.PlaylistDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface IPlaylistService {
    List<Playlist> getPlaylists();

    Playlist createPlaylist(PlaylistDTO playlistDTO);
    String getSongFromApiById(Integer id);

}
