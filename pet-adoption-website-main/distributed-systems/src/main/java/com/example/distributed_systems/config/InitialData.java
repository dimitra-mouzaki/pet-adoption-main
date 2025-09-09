package com.example.distributed_systems.config;

import com.example.distributed_systems.entities.*;
import com.example.distributed_systems.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


//! DONE & FUNCTIONAL
@Component
public class InitialData implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CitizenRepository citizenRepository;
    private final ShelterRepository shelterRepository;
    private final VetRepository vetRepository;
    private final BCryptPasswordEncoder encoder;

    private boolean alreadySetup = false;

    public InitialData(UserRepository userRepository, RoleRepository roleRepository,
                       CitizenRepository citizenRepository, ShelterRepository shelterRepository,
                       VetRepository vetRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.citizenRepository = citizenRepository;
        this.shelterRepository = shelterRepository;
        this.vetRepository = vetRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        try {

            Role roleAdmin = getOrCreateRole("ROLE_ADMIN");
            Role roleBasicUser = getOrCreateRole("ROLE_BASIC");
            Role roleVet = getOrCreateRole("ROLE_VET");
            Role roleShelter = getOrCreateRole("ROLE_SHELTER");

            User admin = getOrCreateUser("admin", "admin@example.com", "admin123", Set.of(roleAdmin));
            createCitizenForUser(admin, "Super", "Admin");

            User shelterUser = getOrCreateUser("shelter1", "shelter1@example.com", "shelter123", Set.of(roleShelter));
            createShelterForUser(shelterUser, "Happy Paws Shelter", 2101234567, "Athens, Greece");

            User vetUser = getOrCreateUser("vet1", "vet1@example.com", "vet123", Set.of(roleVet));
            Citizen vetCitizen = createCitizenForUser(vetUser, "John", "Doe");
            createVetForCitizen(vetCitizen, "VET-12345");

            User basicUser = getOrCreateUser("user1", "user1@example.com", "user123", Set.of(roleBasicUser));
            createCitizenForUser(basicUser, "Alice", "Smith");

            alreadySetup = true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to populate database", e);
        }
    }

    private Role getOrCreateRole(String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            role = new Role(roleName);
            role = roleRepository.save(role);
        }
        return role;
    }

    private User getOrCreateUser(String username, String email, String password, Set<Role> roles) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User(username, email, encoder.encode(password));
            user.setRoles(roles);
            user = userRepository.save(user);
        }
        return user;
    }

    private Citizen createCitizenForUser(User user, String firstName, String lastName) {
        Citizen citizen = citizenRepository.findByUser(user).orElse(null);
        if (citizen == null) {
            citizen = new Citizen();
            citizen.setUser(user);
            citizen.setFirstName(firstName);
            citizen.setLastName(lastName);
            citizen = citizenRepository.save(citizen);
        }
        return citizen;
    }

    private Shelter createShelterForUser(User user, String name, int phoneNumber, String address) {
        Shelter shelter = shelterRepository.findByUser(user).orElse(null);
        if (shelter == null) {
            shelter = new Shelter();
            shelter.setUser(user);
            shelter.setName(name);
            shelter.setPhoneNumber(phoneNumber);
            shelter.setAddress(address);
            shelter.setVerified(true);
            shelter = shelterRepository.save(shelter);
        }
        return shelter;
    }

    private Vet createVetForCitizen(Citizen citizen, String licenseNumber) {
        Vet vet = vetRepository.findByCitizen(citizen).orElse(null);
        if (vet == null) {
            vet = new Vet();
            vet.setCitizen(citizen);
            vet.setLicenseNumber(licenseNumber);
            vet = vetRepository.save(vet);
        }
        return vet;
    }
}