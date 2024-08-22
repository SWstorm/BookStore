package com.example.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "firstname", nullable = false)
    private String firstname;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "lastname")
    private String lastname;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    @Column(name = "login", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
}
