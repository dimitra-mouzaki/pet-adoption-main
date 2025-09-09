package com.example.distributed_systems.repositories;

import com.example.distributed_systems.entities.AdoptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    List<AdoptionRequest> findByCitizenId(Long citizenId);
    List<AdoptionRequest> findByPet_Shelter_Id(Long shelterId);
    List<AdoptionRequest> findByPetId(Long petId);
    void deleteAllByPetIdAndIdNot(Long petId, Long requestId);
}
