package com.example.distributed_systems.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {

    //Columns

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column
    private Long Id;

    @Column
    @NotBlank
    private String name;

    @Column
    @Positive
    private int age;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type animal;

    @Column
    @NotBlank
    private String breed;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Age_Range age_range;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING_APPROVAL;

    @Column
    private boolean healthVerified = false;

    //Mappings

    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    @OneToMany (mappedBy = "pet", cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST})
    @JsonIgnore
    private List<Appointment> petAppointments;

    @OneToMany (mappedBy = "pet", cascade = {CascadeType.DETACH, CascadeType.MERGE,
    CascadeType.PERSIST})
    @JsonIgnore
    private List<AdoptionRequest> requests;


    //Methods
    public Pet() {}

    public Pet(Long id, String name, int age, Type animal, String breed, Gender gender,
               Age_Range age_range, Status status, boolean healthVerified, Shelter shelter,
               HealthRecord healthRecord, List<Appointment> petAppointments,
               List<AdoptionRequest> requests) {
        Id = id;
        this.name = name;
        this.age = age;
        this.animal = animal;
        this.breed = breed;
        this.gender = gender;
        this.age_range = age_range;
        this.status = status;
        this.healthVerified = healthVerified;
        this.shelter = shelter;
        //this.healthRecord = healthRecord;
        this.petAppointments = petAppointments;
        this.requests = requests;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Type getAnimal() {
        return animal;
    }

    public void setAnimal(Type animal) {
        this.animal = animal;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Age_Range getAge_range() {
        return age_range;
    }

    public void setAge_range(Age_Range age_range) {
        this.age_range = age_range;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Appointment> getPetAppointments() {
        return petAppointments;
    }

    public void setPetAppointments(List<Appointment> petAppointments) {
        this.petAppointments = petAppointments;
    }

    public List<AdoptionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AdoptionRequest> requests) {
        this.requests = requests;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public boolean isHealthVerified() {
        return healthVerified;
    }

    public void setHealthVerified(boolean healthVerified) {
        this.healthVerified = healthVerified;
    }

    //Enums

    public enum Gender { MALE, FEMALE }

    public enum Status { PENDING_APPROVAL, APPROVED, ADOPTED }

    public enum Type {DOG, CAT, OTHER}

    public enum Age_Range {YOUNG, MIDDLE_AGED , OLD}


}
