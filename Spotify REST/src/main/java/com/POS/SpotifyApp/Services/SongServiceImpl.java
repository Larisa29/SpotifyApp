package com.POS.SpotifyApp.Services;

import com.POS.SpotifyApp.Controllers.SongController;
import com.POS.SpotifyApp.DataAccess.Exceptions.SongDbIsEmptyException;
import com.POS.SpotifyApp.DataAccess.Exceptions.SongNotFoundException;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.DataAccess.Repositories.IArtistRepository;
import com.POS.SpotifyApp.DataAccess.Repositories.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Optional;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements ISongService{
    @Autowired
    ISongRepository songRepository; //injectez repository

    @Autowired
    IArtistRepository artistRepository;
    @Override
    public List<Song> getAllSongs(Integer page, Integer itemsPerPage)
    {
        Pageable paging = PageRequest.of(page, itemsPerPage);
        Page<Song> pageResult = songRepository.findAll(paging); //findAllSongs??

        return Optional.ofNullable(pageResult.getContent())
                .orElseThrow(() -> new SongDbIsEmptyException()); //daca nu folosesc Optional.ofNullable nu pot folosi orElseThrow(), care tot din clasa Optional
    }
    @Override
    public Song getSong(Integer id) {
        return Optional.ofNullable(songRepository.getSongById(id))
                .orElseThrow(() -> new SongNotFoundException(id));
    }

    @Override
    public EntityModel<Song> convertSongToEntityModel(Song song)
    {
        Link selfLink = linkTo(SongServiceImpl.class).slash(song.getId()).withSelfRel();
        Link parentLink = linkTo(SongServiceImpl.class).withRel("songs");;

        return EntityModel.of(song, selfLink, parentLink);

    }
    @Override
    public Song createSong(Song songRequest)
    {
        //commit and flush at. se face save si imi zice ca e save
        Song savedSong = songRepository.save(songRequest);
        return savedSong;
    }
    @Override
    public Song updateArtistByAddingSong(Integer song, String artistId) {
        return new Song();
    }
//        Artist artist = artistRepository.getArtistById(artistId);
//        if (artist != null)
//        {
//            //creez new song, in caz ca nu exista
//            Song newSong = new Song();//constr.
//            newSong.setName(song.getName());
//            //newSong.setId(song.getId());
//            newSong.setGenre(song.getGenre());
//            newSong.setYear(song.getYear());
//            newSong.setParent(song.getParent());
//            Song saveSong= songRepository.save(newSong);//salvez cantecul creat
//            // pus in tab de leg artist id si savedSog.getId
//
//
//
//            //newSong.getArtists().add(artist);
//            //newSong.setArtists(song.getArtists());
//            artist.getAssignedSongs().add(newSong);
//
//            return songRepository.save(newSong);
//        }
//        else throw new ArtistNotFoundException(artistId);
//    }

    @Override
    public void deleteSongAlbum(int id)
    {
        songRepository.deleteSong(id);
    }
}
