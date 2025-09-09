package com.example.distributed_systems.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Citizen {

    //Columns
    @Id
    @Column
    @JsonIgnore
    private Long Id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    //Mappings
    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "citizen", cascade = {CascadeType.PERSIST,
            CascadeType.DETACH, CascadeType.MERGE})
    @JsonIgnore
    private List<AdoptionRequest> requests;

    @OneToMany(mappedBy = "citizen", cascade = {CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.PERSIST})
    @JsonIgnore
    private List<Appointment> appointments;

    public Citizen(){}

    public Citizen(Long id, String firstName, String lastName, User user,
                   List<AdoptionRequest> requests, List<Appointment> appointments) {
        Id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = user;
        this.requests = requests;
        this.appointments = appointments;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<AdoptionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AdoptionRequest> requests) {
        this.requests = requests;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
