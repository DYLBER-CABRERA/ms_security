package com.ddcf.security.Repositories;

import com.ddcf.security.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role,String> {

}
