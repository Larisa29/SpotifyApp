package com.POS.SpotifyApp.DataAccess.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*
import com.mongodb.lang.Nullable;
*/
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.*;

@Data //genereaza chestiile asociate unui pojo in general: getters, setters, toString, equals
@Entity //daca am clasa Song cu datele ei si vreau sa o pastrez in DB trb definita o entitate recunoscuta de JPA
        //an entity represents a table stored in a database.
        //tre sa aiba un PK si un constructor fara argumente
@Table(name = "melodii") //daca nu puneam asta, numele era implicit numele entitatii(adica Song)
                        //entitatea Song va fi mapata la tabela melodii

public class Song extends RepresentationModel<Song> {
    enum Genre{
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

    enum Types{
        album,
        song,
        single;
    }
    //id-ul e primary key + autoincrement pt id
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY) //.identity
    private Integer id; //pun Integer si nu int pt ca integer e serializabil
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
    public Collection<Artist> getAssignedArtists() {
        return assignedArtists;
    }
    public void removeArtist(Artist artist)
    {
        //this.assignedArtists.remove(artist);

        //remove artist
        this.assignedArtists.remove(artist);
        //also update the song by removing current artist
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
