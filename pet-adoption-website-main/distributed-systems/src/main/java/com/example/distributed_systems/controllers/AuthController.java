package com.example.distributed_systems.controllers;

import com.example.distributed_systems.config.JwtUtils;
import com.example.distributed_systems.entities.*;
import com.example.distributed_systems.payload.*;
import com.example.distributed_systems.repositories.*;
import com.example.distributed_systems.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


//! DONE & FUNCTIONAL
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    RoleRepository roleRepository;
    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    BCryptPasswordEncoder encoder;
    JwtUtils jwtUtils;

    ShelterRepository shelterRepository;
    CitizenRepository citizenRepository;
    VetRepository vetRepository;

    public AuthController(RoleRepository roleRepository, AuthenticationManager authenticationManager,
                          UserRepository userRepository, BCryptPasswordEncoder encoder, JwtUtils jwtUtils,
                          ShelterRepository shelterRepository, CitizenRepository citizenRepository,
                          VetRepository vetRepository) {
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.shelterRepository = shelterRepository;
        this.citizenRepository = citizenRepository;
        this.vetRepository = vetRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt, "Bearer",
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body("Error: Invalid username or password");
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(403).body("Error: User is disabled");
        } catch (org.springframework.security.authentication.LockedException e) {
            return ResponseEntity.status(403).body("Error: User account is locked");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: Authentication failed");
        }
    }

    @PostMapping("/signup/citizen")
    public ResponseEntity<?> registerCitizen(@Valid @RequestBody CitizenSignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body("Username is already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("Email is already in use");

        User user = new User(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()));
        Role basicRole = roleRepository.findByName("ROLE_BASIC")
                .orElseThrow(() -> new RuntimeException("Role BASIC not found"));
        user.setRoles(Set.of(basicRole));
        userRepository.save(user);

        Citizen citizen = new Citizen();
        citizen.setUser(user);
        citizen.setFirstName(request.getFirstName());
        citizen.setLastName(request.getLastName());
        citizenRepository.save(citizen);

        return ResponseEntity.ok("Citizen registered successfully");
    }

    @PostMapping("/signup/vet")
    public ResponseEntity<?> registerVet(@Valid @RequestBody VetSignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body("Username is already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("Email is already in use");

        User user = new User(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()));
        Role vetRole = roleRepository.findByName("ROLE_VET")
                .orElseThrow(() -> new RuntimeException("Role VET not found"));
        user.setRoles(Set.of(vetRole));
        userRepository.save(user);

        Citizen citizen = new Citizen();
        citizen.setUser(user);
        citizen.setFirstName(request.getFirstName());
        citizen.setLastName(request.getLastName());
        citizenRepository.save(citizen);

        Vet vet = new Vet();
        vet.setCitizen(citizen);
        vet.setLicenseNumber(request.getLicenseNumber());
        vetRepository.save(vet);

        return ResponseEntity.ok("Vet registered successfully");
    }

    @PostMapping("/signup/shelter")
    public ResponseEntity<?> registerShelter(@Valid @RequestBody ShelterSignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest().body("Username is already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest().body("Email is already in use");

        User user = new User(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()));
        Role shelterRole = roleRepository.findByName("ROLE_SHELTER")
                .orElseThrow(() -> new RuntimeException("Role SHELTER not found"));
        user.setRoles(Set.of(shelterRole));
        userRepository.save(user);

        Shelter shelter = new Shelter();
        shelter.setUser(user);
        shelter.setName(request.getShelterName());
        shelter.setAddress(request.getShelterAddress());
        shelter.setPhoneNumber(Integer.parseInt(request.getShelterPhone()));
        shelter.setVerified(false);
        shelterRepository.save(shelter);

        return ResponseEntity.ok("Shelter registered successfully");
    }
}