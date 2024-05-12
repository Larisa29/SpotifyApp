package com.example.playlist.Services;

import com.example.playlist.DataAccess.Models.Profile;
import com.example.playlist.View.ProfileDTO;

import java.util.List;

public interface IProfileService {
    List<Profile> getAllUsers();
    Profile createNewProfile(ProfileDTO profileDTO);
}