package com.bmh.lms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user")
@NoArgsConstructor
public class User extends CommonProperties{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column(nullable = false)
    private String username;

    @Column
    private Boolean admin;

    @Column
    private String password;
} 
