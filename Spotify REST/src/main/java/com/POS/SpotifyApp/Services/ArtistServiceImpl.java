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
import java.util.stream.Collectors;

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
    public List<Artist> getAllArtistsFiltered(Optional<String> name, Optional<Boolean> active, Integer page, Integer itemsPerPage)
    {
        List<Artist> allArtists;
        Pageable paging = PageRequest.of(page, itemsPerPage);
        Page<Artist> pageArtist;

        if (name.isPresent() && active.isPresent())
        {
            pageArtist = artistRepository.findByNameContainingAndActive(name.get(), active.get(), paging);
        }
        else if (name.isPresent())
        {
            pageArtist = artistRepository.findByName(name.get(), paging);
        }
        else if (active.isPresent())
        {
            pageArtist = artistRepository.findByActive(active.get(), paging);
        }
        else
        {
            pageArtist = artistRepository.findAll(paging);
        }

        allArtists = pageArtist.getContent();

        if (allArtists.isEmpty())
            throw new ArtistDbIsEmptyException();

        return allArtists;
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
        /*List<Artist> artists = artistRepository.findByName(name);

        if (!artists.isEmpty()) {
            return artists;
        }
        else{
            return Collections.emptyList();
        }*/
        return Collections.emptyList();
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
                Set<Song> assignedSongs = new HashSet<>(artist.getAssignedSongs());
                for (Song song: assignedSongs)
                {
                    artist.removeSong(song);
                    // save modifications in DB
                    artistRepository.save(artist);
                    songRepository.save(song);
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

            artistRepository.save(artist);
            songRepository.save(song);
        }
    }
}

//delete a song associated to an artist

