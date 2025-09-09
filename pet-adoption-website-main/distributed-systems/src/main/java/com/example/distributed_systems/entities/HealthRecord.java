package com.example.distributed_systems.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Boolean healthy;

    @Column
    private String notes;

    @Column
    private LocalDate lastUpdated;

    /*@OneToOne(mappedBy = "healthRecord")
    private Pet pet;*/

    public HealthRecord() {}

    public HealthRecord(Boolean healthy, String notes, LocalDate lastUpdated, Pet pet) {
        this.healthy = false;
        this.notes = notes;
        this.lastUpdated = lastUpdated;
        //this.pet = pet;
    }

    public Long getId() {
        return id;
    }

    public Boolean getHealthy() {
        return healthy;
    }

    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /*public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }*/



}
