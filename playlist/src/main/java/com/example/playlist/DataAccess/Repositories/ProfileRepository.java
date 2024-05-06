package com.example.playlist.DataAccess.Repositories;

import com.example.playlist.DataAccess.Models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String>{

}
