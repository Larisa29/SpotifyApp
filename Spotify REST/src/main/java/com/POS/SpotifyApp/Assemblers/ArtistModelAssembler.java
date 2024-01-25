package com.POS.SpotifyApp.Assemblers;

import com.POS.SpotifyApp.Controllers.ArtistController;
import com.POS.SpotifyApp.DataAccess.Models.Artist;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ArtistModelAssembler implements RepresentationModelAssembler<Artist, EntityModel<Artist>> {
    @Override
    public EntityModel<Artist> toModel(Artist artist)
    {
        return EntityModel.of(artist,
                linkTo(methodOn(ArtistController.class).getArtist(artist.getId())).withSelfRel(),
                linkTo(methodOn(ArtistController.class).getAllArtists(Optional.empty(), Optional.empty())).withRel("artists"));
    }
}
