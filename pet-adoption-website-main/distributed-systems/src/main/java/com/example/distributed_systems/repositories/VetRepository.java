package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Citizen;
import com.example.distributed_systems.entities.Vet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VetRepository extends JpaRepository<Vet, Long> {
    Optional<Vet> findByCitizen(Citizen vetCitizen);

    boolean existsByCitizen(Citizen vetCitizen);
}
