package com.gotprint.notes.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Email(message = "Please provide valid mail id")
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, message = "Minimum length of password should be at-least 8 chat long")
    private String password;

    private String roles = "USER";

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdTimestamp;

    @UpdateTimestamp
    private Timestamp updatedTimestamp;
}
