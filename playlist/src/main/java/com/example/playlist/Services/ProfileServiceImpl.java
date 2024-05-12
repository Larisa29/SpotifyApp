package com.example.playlist.Services;

import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.DataAccess.Repositories.ProfileRepository;
import com.example.playlist.Exceptions.IncorrectRequestBodyExeption;
import com.example.playlist.Exceptions.DuplicateProfileException;
import com.example.playlist.View.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements IProfileService {
    @Autowired
    ProfileRepository profileRepository;

    @Override
    public List<Profile> getAllUsers()
    {
        return profileRepository.findAll();
    }
    @Override
    public Profile createNewProfile(ProfileDTO profileDTO)
    {
        if (profileDTO.getUserId() == null)
        {
            throw new IncorrectRequestBodyExeption("You should specify UserId in your request.");
        }

        if (profileDTO.getEmail() == null)
        {
            throw new IncorrectRequestBodyExeption("You should specify an EMAIL in your request.");
        }

        if (profileDTO.getUserName() == null)
        {
            throw new IncorrectRequestBodyExeption("You should specify an USERNAME in your request.");
        }

        //check if a profile with specified id already exists
        if (profileRepository.findProfileByUserId(profileDTO.getUserId()) == null)
        {
            //////// TO DO: MAKE SOME VALIDATION ON USER INPUT ----> ////
            if (profileDTO.getLikedMusic().size() != 0)
            {
                throw new IncorrectRequestBodyExeption("Unprocessable request.");
            }
            Profile profile = new Profile(profileDTO.getUserId(), profileDTO.getUserName(), profileDTO.getEmail());
            profileRepository.save(profile);

            return profile;
        }
        else
        {
            throw new DuplicateProfileException(profileDTO.getUserId());
        }
    }
}
