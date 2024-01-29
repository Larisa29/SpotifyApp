package com.POS.SpotifyApp.Services;


import com.POS.SpotifyApp.DataAccess.Models.Artist;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IArtistService {

    Artist createArtist(String id, Artist artist);
    List<Artist> getAllArtists(Optional<Integer> page, Optional<Integer> itemsPerPage);
    List<Artist> getAllArtistsFiltered(Optional<String> name, Optional<Boolean> active, Integer page, Integer itemsPerPage);

    Artist getArtist(String id);

    List<Artist> getArtistsByName(String name);
    Artist addSongToArtist(String artistId, Integer songId);
    void deleteArtist(String id);
   // ResponseEntity<Object> updateArtistByAddingSongs(Artist artist, Integer songId);

    void deleteAssociationBetweenSongsAndArtist(String artistId, Integer songId);
}
