package com.example.distributed_systems.controllers;


import com.example.distributed_systems.entities.Appointment;
import com.example.distributed_systems.services.AppointmentService;
import com.example.distributed_systems.services.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//! DONE? & FULLY FUNCTIONAL

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/apt")
public class AppointmentController {

    public AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Secured("ROLE_BASIC")
    @GetMapping("/myappointments")
    public ResponseEntity<List<Appointment>> getMyAppointments(@AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUserId(auth.getId()));
    }

    @Secured("ROLE_SHELTER")
    @GetMapping("/shelterappointments")
    public ResponseEntity<List<Appointment>> getShelterAppointments(@AuthenticationPrincipal UserDetailsImpl auth) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByShelterId(auth.getId()));
    }

    @Secured("ROLE_SHELTER")
    @GetMapping("/petappointments/{petId}")
    public ResponseEntity<List<Appointment>> getPetAppointments(@PathVariable Long petId,
                                                                @AuthenticationPrincipal UserDetailsImpl auth) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPetId(petId, auth.getId());
        return ResponseEntity.ok(appointments);
    }

    @Secured("ROLE_BASIC")
    @PostMapping("/schedule/{petId}")
    public ResponseEntity<String> scheduleAppointment(@PathVariable Long petId,
                                                           @AuthenticationPrincipal UserDetailsImpl auth) {
        appointmentService.scheduleAppointment(auth.getId(), petId);
        return ResponseEntity.ok("Appointment scheduled successfully" );
    }

    @Secured({"ROLE_SHELTER", "ROLE_BASIC"})
    @DeleteMapping("/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId,
                                                    @AuthenticationPrincipal UserDetailsImpl auth) {
        boolean isShelter = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SHELTER"));

        appointmentService.cancelAppointment(appointmentId, auth.getId(), isShelter);
        return ResponseEntity.ok("Appointment cancelled successfully");
    }

}
