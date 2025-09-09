package com.example.distributed_systems.entities;


import jakarta.persistence.*;
@Entity
public class Appointment {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    private Pet pet;

    @ManyToOne
    @JoinColumn
    private Shelter shelter;

    @ManyToOne
    @JoinColumn
    private Citizen citizen;

    public Appointment(){}

    public Appointment(Pet pet, Shelter shelter, Citizen citizen) {
        this.pet = pet;
        this.shelter = shelter;
        this.citizen = citizen;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Shelter getShelter() { return shelter; }

    public void setShelter(Shelter shelter) { this.shelter = shelter; }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }
}
