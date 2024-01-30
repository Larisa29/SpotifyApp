package com.POS.SpotifyApp.DataAccess.Repositories;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ISongRepository extends JpaRepository<Song, Integer>, PagingAndSortingRepository<Song, Integer> {
    @Query(value="SELECT name FROM melodii WHERE year=?1", nativeQuery = true)
    List<Song>  getNameByYear(int year);
    @Query(value="SELECT * FROM melodii LIMIT ?1 OFFSET ?2", nativeQuery = true)
    List<Song> findAllSongs(int items_per_page, int page);

    @Query(value = "SELECT * FROM melodii WHERE id=?1", nativeQuery = true)
    Song getSongById (Integer id);

    @Query(value = "DELETE FROM melodii WHERE id=?1",nativeQuery = true)
    void deleteSong(int id);
    Page<Song> findAll(Specification<Song> specification, Pageable pageable);
}
