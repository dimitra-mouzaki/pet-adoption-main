package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByCitizenId(Long citizenId);
    List<Appointment> findByShelterId(Long shelterId);
    List<Appointment> findByPetId(Long petId);
}
