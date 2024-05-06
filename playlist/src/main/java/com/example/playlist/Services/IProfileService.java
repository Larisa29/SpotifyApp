package com.example.playlist.Services;

import com.example.playlist.DataAccess.Models.Profile;

import java.util.List;

public interface IProfileService {
    Profile save(Profile user);
    List<Profile> getAllUsers();

}