package com.example.playlist.Mappers;

import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.View.ProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
//    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    ProfileDTO profileToProfileDTO(Profile profile);
    Profile dtoToProfile(ProfileDTO profileDTO);
}
