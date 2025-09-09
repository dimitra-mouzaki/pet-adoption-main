package com.example.distributed_systems.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class AdoptionRequest {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;


    public AdoptionRequest() {}

    public AdoptionRequest(Pet pet, Citizen citizen, Status status) {
        this.status = Status.PENDING;
        this.pet = pet;
        this.citizen = citizen;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
