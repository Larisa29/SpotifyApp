package com.example.playlist.Services;

import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.DataAccess.Repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements IProfileService {
    @Autowired
    ProfileRepository profileRepository;

    @Override
    public Profile save(Profile user)
    {
        return profileRepository.save(user);
    }

    @Override
    public List<Profile> getAllUsers()
    {
        return profileRepository.findAll();
    }
}
