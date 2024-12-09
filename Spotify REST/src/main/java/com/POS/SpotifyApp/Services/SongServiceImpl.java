package com.POS.SpotifyApp.Services;

import com.POS.SpotifyApp.Exceptions.SongDbIsEmptyException;
import com.POS.SpotifyApp.Exceptions.SongNotFoundException;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.DataAccess.Models.SongSpecifications;
import com.POS.SpotifyApp.DataAccess.Repositories.IArtistRepository;
import com.POS.SpotifyApp.DataAccess.Repositories.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import java.util.*;
import java.util.Optional;

@Service
public class SongServiceImpl implements ISongService{
    @Autowired
    ISongRepository songRepository; //inject repository

    @Autowired
    IArtistRepository artistRepository;
    @Override
    public List<Song> getAllSongs(Integer page, Integer itemsPerPage)
    {
        Pageable paging = PageRequest.of(page, itemsPerPage);
        Page<Song> pageResult = songRepository.findAll(paging);

        return Optional.ofNullable(pageResult.getContent())
                .orElseThrow(() -> new SongDbIsEmptyException());
    }
    @Override
    public List<Song> getAllSongsFiltered(Optional<String> name, Optional<Integer> year, Optional<Song.Genre> genre, Optional<Song.Types> type, Optional<Integer> page, Optional<Integer> itemsPerPage)
    {
        Specification<Song> specification = Specification.where(null);
        Pageable paging = null;
        if (page.isPresent() && itemsPerPage.isPresent())
        {
            paging = PageRequest.of(page.get(), itemsPerPage.get());
        }
        if (name.isPresent())
        {
            specification = specification.and(SongSpecifications.hasName(name.get()));
        }

        if (year.isPresent())
        {
            specification = specification.and(SongSpecifications.hasYear(year.get()));
        }

        if (genre.isPresent())
        {
            specification = specification.and(SongSpecifications.hasGenre(genre.get()));
        }

        if (type.isPresent())
        {
            specification = specification.and(SongSpecifications.hasType(type.get()));
        }

        List<Song> allSongs;
        if (paging!= null)
        {
            Page<Song> pageSong = songRepository.findAll(specification, paging);
            allSongs = pageSong.getContent();
        }
        else
        {
            allSongs = songRepository.findAll(specification);
        }

        if (allSongs.isEmpty())
            throw new SongDbIsEmptyException();

        return allSongs;
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
        Song savedSong = songRepository.save(songRequest);
        return savedSong;
    }

    @Override
    public Song updateSong(Song songRequest)
    {
        Song song = songRepository.getSongById(songRequest.getId());
        if (song ==null)
        {
            throw new SongNotFoundException(songRequest.getId());
        }

        song.setType(songRequest.getType());
        song.setName(songRequest.getName());
        song.setGenre(songRequest.getGenre());
        song.setParent(songRequest.getParent());
        song.setYear(songRequest.getYear());

        return songRepository.save(song);
    }

    @Override
    public void deleteSongAlbum(int id)
    {
        Song song = songRepository.getSongById(id);
        if (song != null)
        {
            if (!song.getAssignedArtists().isEmpty())
            {
                Set<Artist> assignedArtists = new HashSet<>(song.getAssignedArtists());
                for (Artist artist: assignedArtists)
                {
                    song.removeArtist(artist);
                    songRepository.save(song);
                    artistRepository.save(artist);
                }
            }
        }
        else{
            throw new SongNotFoundException(id);
        }
        songRepository.deleteSong(id);
    }
}
