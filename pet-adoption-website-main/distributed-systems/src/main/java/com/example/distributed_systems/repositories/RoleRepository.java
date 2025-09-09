package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String roleName);

    default Role updateOrInsert(Role role){
        //Checks if the role already exists in the database
        Role existingRole = findByName(role.getName()).orElse(null);

        //If present, the existing role is returned
        if(existingRole != null ){
            return existingRole;
        }else{
            return save(role);
        }
    }
}
