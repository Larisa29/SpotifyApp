package com.POS.SpotifyApp.Services;

import com.POS.SpotifyApp.DataAccess.Exceptions.*;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import com.POS.SpotifyApp.DataAccess.Models.Song;
import com.POS.SpotifyApp.DataAccess.Repositories.IArtistRepository;
import com.POS.SpotifyApp.DataAccess.Repositories.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ArtistServiceImpl implements IArtistService{

    @Autowired
    IArtistRepository artistRepository;

    @Autowired
    ISongRepository songRepository;

    @Override
    public Artist createArtist(String id, Artist artistRequest)
    {
            Optional<Artist> artist = Optional.ofNullable(artistRepository.getArtistById(id));
            //check if the two uuid (from path and body) are the same
            if (!id.equals(artistRequest.getId()))
            {
                throw new DifferentUUIDSException();
            }

            if (artist.isEmpty())
            {   //I don't have the resource, so I must create it
                Artist newArtist  = new Artist(artistRequest.getId(), artistRequest.getName(), artistRequest.getActive());
                newArtist.setId(id);
                return artistRepository.save(newArtist);
            }

            //else resource found, just update it
            if (artistRequest.getId() == null || artistRequest.getName() == null || artistRequest.getActive() == null)
            {
                throw new BadArtistRequestBodyException();
            }

            artist.get().setId(artistRequest.getId());
            artist.get().setName(artistRequest.getName());
            artist.get().setActive(artistRequest.getActive());

            return artistRepository.save(artist.get());
    }

    @Override
    public List<Artist> getAllArtists(Optional<Integer> page, Optional<Integer> itemsPerPage)
    {
        //give default value if page and items per page are missing
        Pageable paging = PageRequest.of(page.orElse(0), itemsPerPage.orElse(1));
        Page<Artist> pageResult = artistRepository.findAll(paging);

        return Optional.ofNullable(pageResult.getContent())
                .orElseThrow(() -> new ArtistDbIsEmptyException());
    }
    @Override
    public Artist getArtist(String id)
    {
        return Optional.ofNullable(artistRepository.getArtistById(id))
                .orElse(null);
    }
    @Override
    public List<Artist> getArtistsByName(String name)
    {
        List<Artist> artists = artistRepository.findByName(name);

        if (!artists.isEmpty()) {
            return artists;
        }
        else{
            return Collections.emptyList();
        }
    }

    @Override
    public Artist addSongToArtist(String artistId, Integer songId)
    {
        Song song = songRepository.getSongById(songId);
        Artist artist = artistRepository.getArtistById(artistId);

        if (song == null)
        {
            throw new SongNotFoundException(songId);
        }

        if (artist == null)
        {
            throw new ArtistNotFoundException(artistId);
        }

        //if a song is already assigned to an artist, then don't duplicate that
        if (!artist.getAssignedSongs().contains(song))
        {
            artist.addSong(song);
        }

        return artistRepository.save(artist);
    }
    @Transactional
    @Override
    public void deleteArtist(String id)
    {
        Artist artist = artistRepository.getArtistById(id);
        if (artist != null)
        {
            if (!artist.getAssignedSongs().isEmpty())
            {
                Set<Song> assignedSongsCopy = new HashSet<>(artist.getAssignedSongs());
                System.out.println("Cantecele asociate lui " + artist.getName()+ " sunt:");
                for (Song song: assignedSongsCopy)
                {
                    System.out.println("song: " + song.getName());
                }

                for (Song song: assignedSongsCopy)
                {
//                    artist.getAssignedSongs().remove(song);
//                    song.getAssignedArtists().remove(artist);
                    artist.removeSong(song);
                    // save modifications in DB
                    artistRepository.save(artist);
                    songRepository.save(song);

                }

                System.out.println("Dupa delete cantecele ramase sunt: \n");
                Set<Song> after_delete = artist.getAssignedSongs();
                for (Song song: after_delete){

                    System.out.println("song: " + song.getName());
                }
            }
        }

        artistRepository.deleteArtistById(id);
    }
    @Override
    public void deleteAssociationBetweenSongsAndArtist(String artistId, Integer songId)
    {
        Artist artist = artistRepository.getArtistById(artistId);
        Song song = songRepository.getSongById(songId);

        if (artist == null)
        {
            throw new ArtistNotFoundException(artistId);
        }
        if (song == null)
        {
            throw new SongNotFoundException(songId);
        }

        if (artist != null && song != null) {
            artist.getAssignedSongs().remove(song);
            song.getAssignedArtists().remove(artist);

            // Salvați modificările în baza de date
            artistRepository.save(artist);
            songRepository.save(song);
        }
    }
}

//delete a song associated to an artist

