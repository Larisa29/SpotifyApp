package com.POS.SpotifyApp.Controllers;

import com.POS.SpotifyApp.DataAccess.Exceptions.SongDbIsEmptyException;
import com.POS.SpotifyApp.DataAccess.Exceptions.SongNotFoundException;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.Services.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URI;
@RestController //datele vor fi  scrise in response body
@RequestMapping("/api/songcollection")
public class SongController{
    @Autowired
    ISongService songService;
    @GetMapping("/songs")
    ResponseEntity<?> getAllSongs(@RequestParam(defaultValue = "1") Integer itemsPerPage,
                                  @RequestParam(defaultValue = "0") Integer page)
    //CollectionModel<EntityModel<Song>> ca tip in loc de ResponseEntity<?> ????
    {
        try {
            List<Song> allSongs = songService.getAllSongs(page, itemsPerPage);

            List<EntityModel<Song>> songs = allSongs.stream()
                    .map(song -> songService.convertSongToEntityModel(song))
                    .collect(Collectors.toList());

            Link selfLink = linkTo(methodOn(SongController.class).getAllSongs(itemsPerPage, page)).withSelfRel();
            return ResponseEntity.ok(CollectionModel.of(songs, selfLink));
        } catch (SongDbIsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/songs/{id}")
    ResponseEntity<?> getSong(@PathVariable Integer id,
                              @RequestParam(required = false, name = "itemsPerPage", defaultValue = "1") int itemsPerPage,
                              @RequestParam(required = false, name = "page", defaultValue = "0") int page)
    {
        try{
            Song song = songService.getSong(id);
            EntityModel<Song> entityModel = songService.convertSongToEntityModel(song);

            return ResponseEntity.ok(entityModel);

        } catch (SongNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/songs")
    ResponseEntity<?> createNewSong(@RequestBody Song song)
    {
        EntityModel<Song> entityModel = songService.convertSongToEntityModel(songService.createSong(song));
        //save the location of the new resource -> SELF link
        URI location = entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri();
        return ResponseEntity
                .created(location)
                .body(entityModel);
    }
    @DeleteMapping("/songs/{id}")
    ResponseEntity<?> deleteSong(@PathVariable int id)
    {
        try{
            Song song = songService.getSong(id);
            songService.deleteSongAlbum(id);
            return ResponseEntity.noContent().build();

        } catch (SongNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



//    @GetMapping("/songs/{year}/name")
//    List<Song> getNameByYear (@PathVariable int year)
//    {
//        return repository.getNameByYear(year);
//    }

    /* TO DO: CONTROLLER FOR UPDATE SONG*/
    /* TO DO: FILTRE LA GET ALL SONGS SAU CEVA SI MAI PARTICULAR??*/
}