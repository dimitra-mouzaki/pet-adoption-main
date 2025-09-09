package com.example.distributed_systems.controllers;

import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.services.ShelterService;
import com.example.distributed_systems.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//! DONE? & FULLY FUNCTIONAL
@RestController
@RequestMapping("/shelter")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @Secured("ROLE_SHELTER") // may need ROLE_BASIC too
    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getShelterPets(@AuthenticationPrincipal UserDetailsImpl auth) {
        Long shelterId = auth.getId();
        return shelterService.getShelterPets(shelterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{shelterId}/approve")
    public ResponseEntity<String> approveShelter(@PathVariable Long shelterId) {
        if (shelterService.approveShelter(shelterId)) {
            return ResponseEntity.ok("Shelter approved successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{shelterId}/reject")
    public ResponseEntity<String> rejectShelter(@PathVariable Long shelterId) {
        if (shelterService.rejectShelter(shelterId)) {
            return ResponseEntity.ok("Shelter rejected successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/unverified")
    public ResponseEntity<List<Shelter>> getUnverifiedShelters() {
        return shelterService.getUnverifiedShelters()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


}
