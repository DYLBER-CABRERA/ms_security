package com.ddcf.security.Repositories;

import com.ddcf.security.Models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    @Query("{'user.$id': ObjectId(?0)}")
    public List<Profile> getProfileByUser(String userId);

}
