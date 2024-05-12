package com.example.playlist.Services;

import com.example.playlist.Clients.IDMClient;
import com.example.playlist.DataAccess.Models.Playlist;
import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.DataAccess.Repositories.PlaylistRepository;
import com.example.playlist.DataAccess.Repositories.ProfileRepository;
import com.example.playlist.Exceptions.PlaylistNameAlreadyExistsException;
import com.example.playlist.Exceptions.ProfileNotFoundException;
import com.example.playlist.Exceptions.SongNotFoundException;
import com.example.playlist.View.PlaylistDTO;
import com.example.playlist.View.SongDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.idmclient.wsdl.UserDTO;
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
    @Autowired
    ProfileRepository profileRepository;

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
    public UserDTO getUserFromSoapById(Integer userId)
    {
        UserDTO response = idmClient.getUserById(userId);
        //System.out.println(response.);
        //String name = response.ge
        return response;
    }

    public Playlist createPlaylist(PlaylistDTO playlistDTO)//playlist dto has a list of ids (music)
    {
        //verific daca mai am vreun playlist cu numele din playlistDTO, pt acelasi user; ca daca 2 useri au acelasi nume la playlists nu e nimic
        //getUserFromSoapById(playlistDTO.getUserId());  //---- CONSUME SOAP

        //check if user exists
        Profile profile = profileRepository.findProfileByUserId(playlistDTO.getUserId());
        if (profile == null)
        {
            throw new ProfileNotFoundException(profile.getUserId());
        }
        //extract playlists for the current user
        List<Playlist> currentUserPlaylists = playlistRepository.findPlaylistByUserId(playlistDTO.getUserId());
        for(Playlist playlist: currentUserPlaylists)
        {
            if (playlist.getPlaylistName().equals(playlistDTO.getPlaylistName()))
            {
                throw new PlaylistNameAlreadyExistsException(playlistDTO.getPlaylistName());
            }
        }

        //if there isn't already a playlist with the name from the request associated with the user, then create playlist
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

        //get songs from SQL using dto
        List<SongDTO> songs = new ArrayList<>();

        List<SongDTO> musicPlaylist = new ArrayList<>();
        for (SongDTO songPlaylistDTO: playlistDTO.getSongs())
        {
            //get song with specific id using RestTemplate
            String songResponse = getSongFromApiById(songPlaylistDTO.getId());
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode songJson = mapper.readTree(songResponse);
                String name = songJson.get("name").asText();
                String selflink = songJson.get("_links").get("self").get("href").asText();

                SongDTO song = new SongDTO(songPlaylistDTO.getId(), name, selflink);

                musicPlaylist.add(song);
            }
            catch (JsonProcessingException e)
            {
                System.out.println("Invalid json!");
            }

            playlist.setSongs(musicPlaylist);
            playlistRepository.save(playlist);
        }
        return playlist;
    }
}
