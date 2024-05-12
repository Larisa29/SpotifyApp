package com.POS.SpotifyApp.DataAccess.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.*;

@Data
@Entity
@Table(name = "melodii")

public class Song extends RepresentationModel<Song> {
    public enum Genre{
        folk,
        rock,
        country,
        pop,
        afro,
        blues,
        jazz,
        classic,
        electronic;
    }

    public enum Types{
        album,
        song,
        single;
    }
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @Enumerated(EnumType.STRING)
    private Genre genre;
    private Integer year;

    @Enumerated(EnumType.STRING)
    private Types type;
    private Integer parent;
    @JsonIgnore
    @ManyToMany(mappedBy = "assignedSongs")
    Set<Artist> assignedArtists = new HashSet<Artist>();
    public Set<Artist> getAssignedArtists() {
        return assignedArtists;
    }
    public void removeArtist(Artist artist)
    {
        //remove artist
        this.assignedArtists.remove(artist);
        //also update the artist by removing current song
        artist.getAssignedSongs().remove(this);
    }
    public void setAssignedArtists(Set<Artist> artists) {
        this.assignedArtists = artists;
    }

    public Song(String name, Genre genre, Integer year, Types type, Integer parent)
    {
        this.name = name;
        this.genre = genre;
        this.year = year;
        this.type = type;
        this.parent = parent;
    }

    public Song() {}

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof Song))
            return false;
        Song song = (Song) obj;
        return Objects.equals(this.id, song.id)
                && Objects.equals(this.name, song.name)
                && Objects.equals(this.genre, song.genre)
                && Objects.equals(this.year, song.year)
                && Objects.equals(this.type, song.type)
                && Objects.equals(this.parent, song.parent);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.name, this.genre, this.year, this.type, this.parent);
    }

}
