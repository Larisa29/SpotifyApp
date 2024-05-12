package com.example.playlist.Controllers;
import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.Exceptions.DuplicateProfileException;
import com.example.playlist.Exceptions.IncorrectRequestBodyExeption;
import com.example.playlist.Mappers.ProfileMapper;
import com.example.playlist.Services.IProfileService;
import com.example.playlist.View.ProfileDTO;
import com.example.playlist.View.SongDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping(value = "/api/profiles")
public class ProfileController {
    private final Logger LOG = LoggerFactory.getLogger(ProfileController.class);
    @Autowired
    private IProfileService profileService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ProfileMapper profileMapper;
    @GetMapping("/users/songs")
    public  List<SongDTO> getUserSongs()
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
        LOG.info("Melodii: " + songs);

        return songs;
    }
    @GetMapping("/get_profiles")
    List<Profile> getUsers()
    {
        LOG.info("Getting all users.");
        return profileService.getAllUsers();
    }

    @PostMapping("/create_profile")
    public ResponseEntity<?> createProfile(@RequestBody ProfileDTO profileDTO)
    {
        try {
            //preiau id din idm ??????
            //default numele ar putea fi username ul din idm, daca nu pot pune un nume diferit pt profil??

            Profile profile = profileService.createNewProfile(profileDTO);

            //map profile to profileDTO and return dto
            ProfileDTO createdProfile = profileMapper.profileToProfileDTO(profile);
            return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
        }
        catch (IncorrectRequestBodyExeption e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (DuplicateProfileException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}