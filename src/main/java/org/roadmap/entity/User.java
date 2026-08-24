package org.roadmap.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private  String username;

    @Column(nullable = false)
    private  String passwordHash;

}
