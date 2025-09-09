package com.example.distributed_systems.services;

import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.repositories.ShelterRepository;
import com.example.distributed_systems.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelterService {

    private ShelterRepository shelterRepository;
    private UserRepository userRepository;

    public ShelterService(ShelterRepository shelterRepository, UserRepository userRepository) {
        this.shelterRepository = shelterRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<List<Shelter>> getUnverifiedShelters() {
        return shelterRepository.findByVerifiedFalse();
    }

    @Transactional
    public Optional<List<Pet>> getShelterPets(Long shelterId) {
        return shelterRepository.findById(shelterId)
                .map(Shelter::getPets);
    }

    @Transactional
    public boolean approveShelter(Long shelterId) {
        return shelterRepository.findById(shelterId)
                .map(shelter -> {
                    shelter.setVerified(true);
                    shelterRepository.save(shelter);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean rejectShelter(Long shelterId) {
        return shelterRepository.findById(shelterId)
                .map(shelter -> {
                    if (shelter.getUser() != null) {
                        shelterRepository.delete(shelter);
                        userRepository.delete(shelter.getUser());
                    }

                    return true;
                })
                .orElse(false);
    }

}


