package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByHealthVerified(Boolean Health);
    List<Pet> findByStatus(Pet.Status status);
}
