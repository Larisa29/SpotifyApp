package com.POS.SpotifyApp.DataAccess.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table(name = "artists")
public class Artist extends RepresentationModel<Artist> {
    @Id
    private String id;
    private String name;
    private Boolean active;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "songs_artists_join_table",
            joinColumns = @JoinColumn(name = "artist_id_fk", referencedColumnName =  "id"),
            inverseJoinColumns =  @JoinColumn(name = "song_id_fk", referencedColumnName = "id")
    )

    private Set<Song> assignedSongs = new HashSet<Song>();
    public Artist(){};
    public Artist(String id, String name, Boolean active)
    {
        this.id = id;
        this.name = name;
        this.active = active;
    }
    public Set<Song> getAssignedSongs() {
        return assignedSongs;
    }
    public void addSong(Song song)
    {
        //add song to current artist
        this.assignedSongs.add(song);
        //also add current artist to assignedArtists list, associated to 'song'
        song.getAssignedArtists().add(this);
    }
    public void removeSong(Song song)
    {
        //remove song from artist's list of songs
        this.assignedSongs.remove(song);
        //also update the song by removing current artist
        song.getAssignedArtists().remove(this);



//        Set<Song> assignedSongsCopy = new HashSet<>(this.assignedSongs);
//        for (Song s : assignedSongsCopy)
//        {
//            this.assignedSongs.remove(s);
//            s.getAssignedArtists().remove(this);
//        }
    }
    public void setAssignedSongs(HashSet<Song> assignedSongs) {
        this.assignedSongs = assignedSongs;
    }

}

