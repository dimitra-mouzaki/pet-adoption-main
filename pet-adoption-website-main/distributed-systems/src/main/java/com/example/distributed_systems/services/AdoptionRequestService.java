package com.example.distributed_systems.services;

import com.example.distributed_systems.entities.AdoptionRequest;
import com.example.distributed_systems.entities.Citizen;
import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.repositories.AdoptionRequestRepository;
import com.example.distributed_systems.repositories.CitizenRepository;
import com.example.distributed_systems.repositories.PetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetRepository petRepository;
    private final CitizenRepository citizenRepository;

    public AdoptionRequestService(AdoptionRequestRepository adoptionRequestRepository,
                                  PetRepository petRepository,
                                  CitizenRepository citizenRepository) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.petRepository = petRepository;
        this.citizenRepository = citizenRepository;
    }

    @Transactional
    public AdoptionRequest submitRequest(Long petId, Long citizenId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        AdoptionRequest request = new AdoptionRequest(pet, citizen, AdoptionRequest.Status.PENDING);
        return adoptionRequestRepository.save(request);
    }

    @Transactional
    public boolean deleteRequest(Long requestId) {
        return adoptionRequestRepository.findById(requestId)
                .map(request -> {
                    adoptionRequestRepository.delete(request);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public List<AdoptionRequest> getMyRequests(Long citizenId) {
        return adoptionRequestRepository.findByCitizenId(citizenId);
    }

    @Transactional
    public List<AdoptionRequest> getShelterRequests(Long shelterId) {
        return adoptionRequestRepository.findByPet_Shelter_Id(shelterId);
    }

    @Transactional
    public List<AdoptionRequest> getPetRequests(Long petId) {
        return adoptionRequestRepository.findByPetId(petId);
    }

    @Transactional
    public boolean approveRequest(Long requestId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Pet pet = request.getPet();
        pet.setStatus(Pet.Status.ADOPTED);
        petRepository.save(pet);

        adoptionRequestRepository.deleteAllByPetIdAndIdNot(pet.getId(), requestId);

        request.setStatus(AdoptionRequest.Status.APPROVED);
        adoptionRequestRepository.save(request);

        return true;
    }
}
