package com.POS.SpotifyApp.Controllers;

import com.POS.SpotifyApp.Assemblers.ArtistModelAssembler;
import com.POS.SpotifyApp.DataAccess.Exceptions.*;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.Services.IArtistService;
import com.POS.SpotifyApp.Services.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
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
                                           @RequestParam(defaultValue = "1") Optional<Integer> itemsPerPage,
                                           @RequestParam(required = false) Optional<String> name,
                                           @RequestParam(required = false) Optional<Boolean> active)
    {
        try {
            List<Artist> allArtists = artistService.getAllArtistsFiltered(name, active, page.orElse(0), itemsPerPage.orElse(1));
            List<EntityModel<Artist>> artists = allArtists.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            Link selfLink = linkTo(methodOn(ArtistController.class).getAllArtists(page, itemsPerPage, Optional.empty(), Optional.empty())).withSelfRel();
            return ResponseEntity.ok(CollectionModel.of(artists, selfLink));
        }
        catch (ArtistDbIsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/artists/{id}")
    public ResponseEntity<?> getArtist(@PathVariable String id)
    {
        try{
            Artist artist = artistService.getArtist(id);
            return ResponseEntity.ok(assembler.toModel(artist));
        }
        catch (ArtistNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/artists/filter")
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
    ResponseEntity<?> getArtistSongsById(@PathVariable String id)
    {
        Artist artist = artistService.getArtist(id);
        Collection<Song> artistSongs = artist.getAssignedSongs();

        Link selfLink = linkTo(methodOn(ArtistController.class).getArtistSongsById(id)).withSelfRel();
        Link parentLink = linkTo(methodOn(ArtistController.class).getArtist(id)).withRel("parent");

        if(!artistSongs.isEmpty())
        {
            List<EntityModel<Song>> songs = artistSongs.stream()
                    .map(song -> songService.convertSongToEntityModel(song))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(CollectionModel.of(songs, selfLink, parentLink));
        }
        else
        {
            return ResponseEntity.notFound().header("Links", selfLink.toString() + ", " + parentLink.toString())
                                 .build();
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
            artistService.deleteArtist(id);
            return ResponseEntity.noContent().build();

        } catch (ArtistNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/artists/{artistId}/song/{songId}")
    ResponseEntity<?> deleteAssociation(@PathVariable String artistId, @PathVariable Integer songId)
    {
        try{
            artistService.deleteAssociationBetweenSongsAndArtist(artistId, songId);
            return ResponseEntity.noContent().build();

        } catch (ArtistNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (SongNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
