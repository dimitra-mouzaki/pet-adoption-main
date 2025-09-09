package com.example.distributed_systems.services;

import com.example.distributed_systems.entities.HealthRecord;
import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.repositories.HealthRecordRepository;
import com.example.distributed_systems.repositories.PetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
public class PetService {

    private PetRepository petRepository;
    private HealthRecordRepository healthRecordRepository;

    public PetService(PetRepository petRepository,
                      HealthRecordRepository healthRecordRepository) {
        this.petRepository = petRepository;
        this.healthRecordRepository = healthRecordRepository;
    }

    @Transactional
    public Optional<Pet> getPet(Long id){ return (petRepository.findById(id)); }

    @Transactional
    public List<Pet> getPets(){ return petRepository.findAll(); }

    @Transactional
    public void savePet(Pet pet){ petRepository.save(pet); }

    @Transactional
    public void validatePetHealth(Long petId) {
        Optional<Pet> petOpt = petRepository.findById(petId);
        if (petOpt.isEmpty()) {
            throw new RuntimeException("Pet not found with id: " + petId);
        }

        Pet pet = petOpt.get();

        pet.setHealthVerified(true);
        petRepository.save(pet);
    }

    @Transactional
    public void approvePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        pet.setStatus(Pet.Status.APPROVED);
        petRepository.save(pet);
    }

    @Transactional
    public void rejectPet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        petRepository.delete(pet);
    }

    @Transactional
    public List<Pet> filterPets(Pet.Type type, Pet.Gender gender, Pet.Age_Range ageRange) {
        return petRepository.findAll().stream()
                .filter(pet -> type == null || pet.getAnimal() == type)
                .filter(pet -> gender == null || pet.getGender() == gender)
                .filter(pet -> ageRange == null || pet.getAge_range() == ageRange)
                .toList();
    }

}
