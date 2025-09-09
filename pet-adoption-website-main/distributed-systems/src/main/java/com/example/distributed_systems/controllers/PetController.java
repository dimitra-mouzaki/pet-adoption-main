package com.example.distributed_systems.controllers;

import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.repositories.PetRepository;
import com.example.distributed_systems.repositories.ShelterRepository;
import com.example.distributed_systems.services.PetService;
import com.example.distributed_systems.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//! DONE? & FULLY FUNCTIONAL
@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/pet")
public class PetController {

    private PetService petService;
    private ShelterRepository shelterRepository;

    private PetRepository petRepository;

    public PetController(PetService petService, ShelterRepository shelterRepository,
                         PetRepository petRepository) {
        this.petService = petService;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/all")
    public ResponseEntity<?> getPets(){
        try{
            List<Pet> pets = petService.getPets();

            if (pets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pets found.");
            }

            return ResponseEntity.ok(pets);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingPets() {
        try {
            List<Pet> pendingPets = petService.getPets().stream()
                    .filter(pet -> pet.getStatus() == Pet.Status.PENDING_APPROVAL)
                    .toList();

            if (pendingPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pets pending approval found.");
            }

            return ResponseEntity.ok(pendingPets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/approve/{petId}")
    public ResponseEntity<String> approvePet(@PathVariable Long petId) {
        try {
            petService.approvePet(petId);
            return ResponseEntity.ok("Pet approved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping ("/reject/{petId}")
    public ResponseEntity<String> rejectPet(@PathVariable Long petId) {
        try {
            petService.rejectPet(petId);
            return ResponseEntity.ok("Pet rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_SHELTER")
    @PostMapping("")
    public ResponseEntity<String> addPet(@RequestBody Pet pet,
                                         @AuthenticationPrincipal UserDetailsImpl auth) {
        try {
            Shelter shelter = shelterRepository.findById(auth.getId())
                    .orElseThrow(() -> new RuntimeException("Shelter not found."));

            if (!shelter.getVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to add pets. Shelter is not verified.");
            }

            pet.setShelter(shelter);
            pet.setStatus(Pet.Status.PENDING_APPROVAL);
            pet.setHealthVerified(false);

            if (pet.getAge() >= 0 && pet.getAge() <= 2) {
                pet.setAge_range(Pet.Age_Range.YOUNG);
            } else if (pet.getAge() <= 7) {
                pet.setAge_range(Pet.Age_Range.MIDDLE_AGED);
            } else {
                pet.setAge_range(Pet.Age_Range.OLD);
            }

            petRepository.save(pet);

            return ResponseEntity.status(HttpStatus.CREATED).body("Pet added successfully.");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_VET")
    @GetMapping("/unhealthy")
    public ResponseEntity<?> getUnhealthyPets() {
        try {
            List<Pet> unhealthyPets = petService.getPets().stream()
                    .filter(pet -> !pet.isHealthVerified())
                    .toList();

            if (unhealthyPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No unhealthy pets found.");
            }

            return ResponseEntity.ok(unhealthyPets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @Secured("ROLE_VET")
    @PutMapping("/validate/{petId}")
    public ResponseEntity<String> validatePetHealth(@PathVariable Long petId) {
        try {
            petService.validatePetHealth(petId);
            return ResponseEntity.ok("Pet health validated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<Pet> getPet(@PathVariable Long petId){
        Optional<Pet> pet = petService.getPet(petId);
        return pet.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailablePets() {
        try {
            List<Pet> availablePets = petService.getPets().stream()
                    .filter(pet -> pet.getStatus() == Pet.Status.APPROVED && pet.isHealthVerified())
                    .toList();

            if (availablePets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available pets found.");
            }

            return ResponseEntity.ok(availablePets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterPets(
            @RequestParam(required = false) Pet.Type type,
            @RequestParam(required = false) Pet.Gender gender,
            @RequestParam(required = false) Pet.Age_Range ageRange) {

        try {
            List<Pet> filteredPets = petService.filterPets(type, gender, ageRange);

            if (filteredPets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pets found with the given filters.");
            }

            return ResponseEntity.ok(filteredPets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
