package com.mustafa.laboration2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kategorier", uniqueConstraints = @UniqueConstraint(columnNames = "namn"))
public class Kategori {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String namn;

    private String symbol;
    private String beskrivning;

    // Getters och setters
    // ...
}
