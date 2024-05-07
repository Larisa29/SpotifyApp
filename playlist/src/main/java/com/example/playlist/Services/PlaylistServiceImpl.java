package com.example.playlist.Services;

import com.example.playlist.Clients.IDMClient;
import com.example.playlist.DataAccess.Models.Playlist;
import com.example.playlist.DataAccess.Models.SongDetails;
import com.example.playlist.DataAccess.Repositories.PlaylistRepository;
import com.example.playlist.Exceptions.SongNotFoundException;
import com.example.playlist.View.PlaylistDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.idmclient.wsdl.GetUserByIdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.playlist.Exceptions.IncorrectRequestBodyExeption;
import com.example.playlist.Enums.Visibility;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Service
public class PlaylistServiceImpl implements IPlaylistService{
    @Autowired
    public PlaylistRepository playlistRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    IDMClient idmClient;

    public List<Playlist> getPlaylists()
    {
        return playlistRepository.findAll();
    }

    //AAS PUTEA PUNE ASTA IN Clients/ restClient??
    public String getSongFromApiById(Integer id) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    "http://127.0.0.1:8080/api/songcollection/songs/" + id,
                    HttpMethod.GET,
                    null,
                    String.class);

            String response = responseEntity.getBody(); //get all data as a string
            return response;
        }
        catch (HttpClientErrorException e){
            throw new SongNotFoundException(id);
        }
    }
    public void getUserFromSoapById(Integer userId)
    {
        GetUserByIdResponse response = idmClient.getUserById(userId);
    }

    public Playlist createPlaylist(PlaylistDTO playlistDTO)//playlist dto has a list of ids (music)
    {
        //verific daca mai am vreun playlist cu numele din playlistDTO, pt acelasi user; ca daca 2 useri au acelasi nume la playlists nu e nimic
        getUserFromSoapById(playlistDTO.getUserId());


        //daca nu exista, creez playlist cu datele din PlaylistDTO
        Playlist playlist = new Playlist(playlistDTO.getUserId(), playlistDTO.getPlaylistName());
        if (playlistDTO.getVisibility() != null)
        {
            playlist.setVisibility(playlistDTO.getVisibility());
        }
        else
        {
            throw new IncorrectRequestBodyExeption("Incorrect request body: you must set visisbility field!");
        }
        if(playlist.getVisibility() == Visibility.Unknown)
        {
            throw new IncorrectRequestBodyExeption("Visibility field is incorrect - it should be PPublic/PPrivate/Friends");
        }
        //apoi preiau melodiile din SQL folosindu-ma de id-urile din DTO
        List<SongDetails> songs = new ArrayList<>();
        //persist schimbarile in NOSql
        List<SongDetails> musicPlaylist = new ArrayList<>();
        for (SongDetails songPlaylistDTO: playlistDTO.getSongs())
        {
            //get song with specific id using RestTemplate
            String songResponse = getSongFromApiById(songPlaylistDTO.getId());
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode songJson = mapper.readTree(songResponse);
                String name = songJson.get("name").asText();
                String selflink = songJson.get("_links").get("self").get("href").asText();

                SongDetails song = new SongDetails(songPlaylistDTO.getId(), name, selflink);

                musicPlaylist.add(song);
            }
            catch (JsonProcessingException e)
            {
                System.out.println("Invalid json!");
            }

            playlist.setSongs(musicPlaylist);

        }
        return playlist;
    }
}
