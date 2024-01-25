package com.POS.SpotifyApp.Controllers;

import com.POS.SpotifyApp.Assemblers.ArtistModelAssembler;
import com.POS.SpotifyApp.DataAccess.Exceptions.*;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.Services.IArtistService;
import com.POS.SpotifyApp.Services.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin
@RestController
@RequestMapping("/api/songcollection")
public class ArtistController {
    @Autowired
    IArtistService artistService;

    @Autowired
    ISongService songService;

    @Autowired
    ArtistModelAssembler assembler;

    @GetMapping("/artists")
    public ResponseEntity<?> getAllArtists(@RequestParam(defaultValue = "0") Optional<Integer> page,
                                           @RequestParam(defaultValue = "1") Optional<Integer> itemsPerPage)
    {
        try {
            List<Artist> allArtists = artistService.getAllArtists(page, itemsPerPage);

            List<EntityModel<Artist>> artists = allArtists.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            Link selfLink = linkTo(methodOn(ArtistController.class).getAllArtists(page, itemsPerPage)).withSelfRel();
            return ResponseEntity.ok(CollectionModel.of(artists, selfLink));
        }
        catch (ArtistDbIsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<?> getArtist(@PathVariable String id)
    {   /* TO DO: ADD HATEOAS*/
        try{
            Artist artist = artistService.getArtist(id);
            return ResponseEntity.ok(assembler.toModel(artist));
        }
        catch (ArtistNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/artists/filter")
    /* TO DO: ADD HATEOAS*/
    ResponseEntity<List<Artist>> getAllArtistsByName(@RequestParam String name)
    {
        List<Artist> artists = artistService.getArtistsByName(name);

        if (!artists.isEmpty())
        {
            return ResponseEntity.ok(artists);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/artists/{id}")
    ResponseEntity<?> createOrUpdateArtist(@RequestBody Artist artist, @PathVariable String id) {
        try {
            //update artist
            Artist updatedArtist = artistService.getArtist(id);
            if (updatedArtist != null) {
                updatedArtist = artistService.createArtist(id, artist);
                return new ResponseEntity<>(updatedArtist, HttpStatus.NO_CONTENT);
            }
            else {
                //create artist
                Artist newArtist = artistService.createArtist(id, artist);
                return new ResponseEntity<>(newArtist, HttpStatus.CREATED);
            }
        }
        catch (DifferentUUIDSException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body((e.getMessage()));
        }
        catch (BadArtistRequestBodyException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PutMapping("/artists/{artistId}/songs/{songId}")
    ResponseEntity<?> addSongToArtist(@PathVariable String artistId, @PathVariable Integer songId)
    {
        //assign song with id = songId to the artist with id = artistId
        try {
            artistService.addSongToArtist(artistId, songId);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        catch (SongNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (ArtistNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/artists/{id}/songs")
    ResponseEntity<Collection<Song>> getArtistSongsById(@PathVariable String id)
    {
        /* TO DO: ADD HATEOAS*/
        Artist artist = artistService.getArtist(id);
        Collection<Song> artistSongs = artist.getAssignedSongs();

        if(!artistSongs.isEmpty())
        {
            return ResponseEntity.ok(artistSongs);
        }
        else
        {
            return new ResponseEntity(artistSongs, HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/artists/{id}")
    ResponseEntity<?> deleteArtist(@PathVariable String id)
    {
        try{
            Artist artist = artistService.getArtist(id);
            if (artist == null)
            {
                throw new ArtistNotFoundException(id);
            }

//            if (!artist.getAssignedSongs().isEmpty())
//            {
//            Set<Song> assignedSongsCopy = new HashSet<>(artist.getAssignedSongs());
//            System.out.println("Cantecele asociate lui " + artist.getName()+ " sunt:");
//            for (Song song: assignedSongsCopy){
//                System.out.println("song: " + song.getName());
//                }
//
//            for (Song song: assignedSongsCopy){
//                song.getAssignedArtists().remove(artist);
//                artist.removeSong(song);
//
//            }
//            System.out.println("Dupa delete cantelecle ramase sunt: ");
//            Set<Song> after_delete = artist.getAssignedSongs();
//            for (Song song: after_delete){
//
//                System.out.println("song: " + song.getName());
//            }
//                //artist.getAssignedSongs().clear();
//            }
//
//            artistService.deleteArtist(id);
            artistService.deleteArtist(id);
            return ResponseEntity.noContent().build();

        } catch (ArtistNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
