package com.example.distributed_systems.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Integer getId() { return Id; }

    public void setId(Integer id) { Id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
