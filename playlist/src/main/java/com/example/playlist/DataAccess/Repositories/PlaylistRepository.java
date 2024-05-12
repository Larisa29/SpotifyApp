package com.example.playlist.DataAccess.Repositories;

import com.example.playlist.DataAccess.Models.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {
    List<Playlist> findPlaylistByUserId(Integer userId);
}
