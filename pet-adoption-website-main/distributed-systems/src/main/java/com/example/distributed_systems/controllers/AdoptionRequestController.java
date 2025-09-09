package com.example.distributed_systems.controllers;

import com.example.distributed_systems.entities.AdoptionRequest;
import com.example.distributed_systems.services.AdoptionRequestService;
import com.example.distributed_systems.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO deleteRequest causing 403 when secured
//! DONE? & PARTLY FUNCTIONAL
@RestController
@RequestMapping("/requests")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @Secured("ROLE_BASIC")
    @PostMapping("/submit/{petId}")
    public ResponseEntity<AdoptionRequest> submitRequest(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetailsImpl auth) {
        AdoptionRequest request = adoptionRequestService.submitRequest(petId, auth.getId());
        return ResponseEntity.ok(request);
    }

    @Secured("ROLE_BASIC")
    @GetMapping("/myrequests")
    public ResponseEntity<List<AdoptionRequest>> getMyRequests(
            @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(adoptionRequestService.getMyRequests(auth.getId()));
    }

    // TODO CAUSES 403: @Secured("ROLE_SHELTER, ROLE_BASIC") ?????
    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long requestId) {
        if (adoptionRequestService.deleteRequest(requestId)) {
            return ResponseEntity.ok("Request deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured("ROLE_SHELTER")
    @GetMapping("/shelter")
    public ResponseEntity<List<AdoptionRequest>> getShelterRequests(
            @AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(adoptionRequestService.getShelterRequests(auth.getId()));
    }

    @Secured("ROLE_SHELTER")
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AdoptionRequest>> getPetRequests(@PathVariable Long petId) {
        return ResponseEntity.ok(adoptionRequestService.getPetRequests(petId));
    }

    @Secured("ROLE_SHELTER")
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approveRequest(@PathVariable Long requestId) {
        if (adoptionRequestService.approveRequest(requestId)) {
            return ResponseEntity.ok("Request approved and pet adopted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
