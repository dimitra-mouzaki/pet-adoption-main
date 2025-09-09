package com.example.distributed_systems.services;

import com.example.distributed_systems.entities.Appointment;
import com.example.distributed_systems.entities.Citizen;
import com.example.distributed_systems.entities.Pet;
import com.example.distributed_systems.entities.Shelter;
import com.example.distributed_systems.repositories.AppointmentRepository;
import com.example.distributed_systems.repositories.CitizenRepository;
import com.example.distributed_systems.repositories.PetRepository;
import com.example.distributed_systems.repositories.ShelterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CitizenRepository citizenRepository;
    private final PetRepository petRepository;
    private final ShelterRepository shelterRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              CitizenRepository citizenRepository,
                              PetRepository petRepository,
                              ShelterRepository shelterRepository) {
        this.appointmentRepository = appointmentRepository;
        this.citizenRepository = citizenRepository;
        this.petRepository = petRepository;
        this.shelterRepository = shelterRepository;
    }

    @Transactional
    public List<Appointment> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByCitizenId(userId);
    }

    @Transactional
    public List<Appointment> getAppointmentsByShelterId(Long shelterId) {
        return appointmentRepository.findByShelterId(shelterId);
    }

    @Transactional
    public List<Appointment> getAppointmentsByPetId(Long petId, Long shelterId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        // Έλεγχος ότι το pet ανήκει στο συγκεκριμένο shelter
        if (!pet.getShelter().getId().equals(shelterId)) {
            throw new RuntimeException("You are not allowed to view appointments for this pet");
        }

        return appointmentRepository.findByPetId(petId);
    }

    @Transactional
    public Appointment scheduleAppointment(Long citizenId, Long petId) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        Shelter shelter = pet.getShelter();

        Appointment appointment = new Appointment(pet, shelter, citizen);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long requesterId, boolean isShelter) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (isShelter) {
            if (!appointment.getShelter().getId().equals(requesterId)) {
                throw new RuntimeException("You are not allowed to cancel this appointment");
            }
        } else {
            if (!appointment.getCitizen().getId().equals(requesterId)) {
                throw new RuntimeException("You are not allowed to cancel this appointment");
            }
        }

        appointmentRepository.delete(appointment);
    }
}

