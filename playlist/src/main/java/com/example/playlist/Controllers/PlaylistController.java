package com.example.playlist.Controllers;

import com.example.playlist.DataAccess.Models.Playlist;
import com.example.playlist.Exceptions.IncorrectRequestBodyExeption;
import com.example.playlist.Exceptions.PlaylistNameAlreadyExistsException;
import com.example.playlist.Exceptions.ProfileNotFoundException;
import com.example.playlist.Services.IPlaylistService;
import com.example.playlist.View.PlaylistDTO;
import com.example.playlist.View.ProfileDTO;
import com.example.playlist.View.SongDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.idmclient.wsdl.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/songscollection")
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/playlists")
    public ResponseEntity<?> createNewPlaylist(@RequestBody PlaylistDTO playlistDTO)
    {
        try{
            Playlist playlist = playlistService.createPlaylist(playlistDTO);
            return new ResponseEntity<>(playlist, HttpStatus.CREATED);
        }
        catch (ProfileNotFoundException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (PlaylistNameAlreadyExistsException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch(IncorrectRequestBodyExeption e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
    }

    @GetMapping("/playlists/user")
    public ResponseEntity<?> getUserInfo(@RequestBody PlaylistDTO playlistDTO)
    {
        UserDTO user = playlistService.getUserFromSoapById(playlistDTO.getUserId());
        BigInteger userId = user.getUserId().getValue();
        Integer userIdToInt = userId.intValue();
        ProfileDTO profileDTO = new ProfileDTO(userIdToInt, user.getUserName().getValue(),null);
        return new ResponseEntity<>(profileDTO, HttpStatus.OK);

    }


    @GetMapping("/songs")
    public  List<SongDTO> getUserSongs() //ASTA AR FI UTIL CA SA CREEZ UN PLAYLIST CU TOATE PIESELE DIN BAZA MEA DE DATE sau sa returnez toate melodiile pe care le am in SQL!!!
    {
        List<SongDTO> songs = new ArrayList<>();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://127.0.0.1:8080/api/songcollection/songs",
                HttpMethod.GET,
                null,
                String.class);
        String json = responseEntity.getBody(); //get all data as a string
        //LOG.info("melodii: " + json);

        //parse json and transform any Song in SongDetails
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            JsonNode allSongsNode = jsonNode.get("_embedded").get("songs");

            for (JsonNode songNode: allSongsNode){
                String name = songNode.get("name").asText();
                String url = songNode.get("_links").get("self").get("href").asText();
                int lastIndex = url.lastIndexOf('/');
                Integer id = Integer.parseInt(url.substring(lastIndex + 1)); //get the id from url

                String link = songNode.get("_links").get("self").get("href").asText();
                SongDTO song = new SongDTO(id, name, link);
                songs.add(song);
            }
        }catch (JsonProcessingException e){
            System.out.println("json processing error!");
        }

        return songs;
    }

//    @PostMapping("/playlists")
//    ResponseEntity<Playlist> createPlaylist(@RequestBody Playlist playlist)
//    {
//        playlistService.createPlaylist(playlist);
//        return new ResponseEntity(null, HttpStatus.ACCEPTED);
//    }
//
//    @GetMapping("/playlists")
//    List<Playlist> getPlaylists()
//    {
//        return playlistService.getPlaylists();
//    }

}