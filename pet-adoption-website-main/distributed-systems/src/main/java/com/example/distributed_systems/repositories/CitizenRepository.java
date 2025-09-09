package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Citizen;
import com.example.distributed_systems.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByUser(User user);

}
