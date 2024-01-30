package com.POS.SpotifyApp.DataAccess.Models;
import org.springframework.data.jpa.domain.Specification;
import com.POS.SpotifyApp.DataAccess.Models.Song.Genre;
import com.POS.SpotifyApp.DataAccess.Models.Song.Types;

public class SongSpecifications {
    public static Specification<Song> hasName(String name){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), name));
    }

    public static Specification<Song> hasYear(Integer year){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("year"), year));
    }

    public static Specification<Song> hasGenre(Genre genre){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("genre"), genre));
    }

    public static Specification<Song> hasType(Types type){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type));
    }
}
