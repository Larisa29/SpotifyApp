package com.POS.SpotifyApp.DataAccess.Repositories;

import com.POS.SpotifyApp.DataAccess.Models.Song;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//ar trb sa specific "the domain type" si tipul id-ului intre <>
public interface ISongRepository extends JpaRepository<Song, Integer>, PagingAndSortingRepository<Song, Integer> {
    //in interfata de aici scriu queries native JPA cu parametri ca sa preiau date din DB
    @Query(value="SELECT name FROM melodii WHERE year=?1", nativeQuery = true)
    List<Song>  getNameByYear(int year);
    @Query(value="SELECT * FROM melodii LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<Song> findAllSongs(int items_per_page,int page);

    @Query(value = "SELECT * FROM melodii WHERE id=?1", nativeQuery = true)
    Song getSongById (Integer id);

    @Query(value = "DELETE FROM melodii WHERE id=?1",nativeQuery = true)
    void deleteSong(int id);
}
