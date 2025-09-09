package com.example.distributed_systems.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;


@Entity
public class Shelter {

    //Columns
    @Id
    private Long Id;

    @Column
    @NotBlank
    private String name;

    @Column(nullable = false)
    private int phoneNumber;

    @Column
    @NotBlank
    private String address;

    @Column(nullable = false)
    @JsonIgnore
    private Boolean verified = false;

    //Mappings
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "shelter", cascade = {CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    private List<Pet> pets;

    //Methods
    public Shelter(){}

    public Shelter(Long id, String name, int phoneNumber, String address,
                   Boolean verified, User user, List<Pet> pets) {
        Id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.verified = verified;
        this.user = user;
        this.pets = pets;
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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
