package com.POS.SpotifyApp.Services;

import com.POS.SpotifyApp.DataAccess.Models.Song;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import java.util.Optional;

import java.util.List;

public interface ISongService{
     Song createSong(Song song);
     List<Song> getAllSongs(Integer page,Integer itemsPerPage);
     List<Song> getAllSongsFiltered(Optional<String> name, Optional<Integer> year, Optional<Song.Genre> genre, Optional<Song.Types> type, Integer page, Integer itemsPerPage);
     Song getSong(Integer id);
     EntityModel<Song> convertSongToEntityModel(Song song);
     void deleteSongAlbum(int id);

}
