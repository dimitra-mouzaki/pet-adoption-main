package com.example.distributed_systems.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "vets")
public class Vet {

    @Id
    private Long citizenId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "citizen_id")
    @JsonIgnore
    private Citizen citizen;

    @Column
    @NotBlank
    private String licenseNumber;


    public Vet(){}

    public Vet(Long citizenId, Citizen citizen, String licenseNumber) {
        this.citizenId = citizenId;
        this.citizen = citizen;
        this.licenseNumber = licenseNumber;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
