//package com.example.playlist.Controllers;
//import com.example.playlist.DataAccess.Models.SongDetails;
//import com.example.playlist.DataAccess.Models.Profile;
//import com.example.playlist.Services.IProfileService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//@RestController
//@RequestMapping(value = "/api/")
//public class ProfileController {
//    private final Logger LOG = LoggerFactory.getLogger(ProfileController.class);
//    @Autowired
//    private IProfileService userService;
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @GetMapping("/users/songs")
//    public  List<SongDetails> getUserSongs()
//    {
//        List<SongDetails> songs = new ArrayList<>();
//        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                "http://127.0.0.1:8080/api/songcollection/songs",
//                HttpMethod.GET,
//                null,
//                String.class);
//        String json = responseEntity.getBody(); //get all data as a string
//        //LOG.info("melodii: " + json);
//
//        //parse json and transform any Song in SongDetails
//        try{
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode jsonNode = mapper.readTree(json);
//            JsonNode allSongsNode = jsonNode.get("_embedded").get("songs");
//
//            for (JsonNode songNode: allSongsNode){
//                String name = songNode.get("name").asText();
//                String url = songNode.get("_links").get("self").get("href").asText();
//                int lastIndex = url.lastIndexOf('/');
//                Integer id = Integer.parseInt(url.substring(lastIndex + 1)); //get the id from url
//
//                String link = songNode.get("_links").get("self").get("href").asText();
//                SongDetails song = new SongDetails(id, name, link);
//                songs.add(song);
//            }
//        }catch (JsonProcessingException e){
//            System.out.println("json processing error!");
//        }
//        LOG.info("Melodii: " + songs);
//
//        return songs;
//    }
//    @GetMapping("/users")
//    List<Profile> getUsers()
//    {
//        LOG.info("Getting all users.");
//        return userService.getAllUsers();
//    }
//
////    @PostMapping("/create")
////    public User addUser(@RequestBody User user)
////    {
////        LOG.info("Saving user. ");
////        return userService.save(user);
////    }
//
//
//}