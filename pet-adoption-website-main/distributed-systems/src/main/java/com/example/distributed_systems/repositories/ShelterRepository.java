package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    Optional<Shelter> findByUserId(Long Long);
    Optional<Shelter> findByUser(User shelterUser);
    Optional<List<Shelter>> findByVerifiedFalse();

}
