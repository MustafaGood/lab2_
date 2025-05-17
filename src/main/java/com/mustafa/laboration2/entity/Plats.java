package com.mustafa.laboration2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platser")
public class Plats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @ManyToOne
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    private String anvandarId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PUBLIK;

    private LocalDateTime datumSkapad;
    private LocalDateTime datumAndrad;
    private String beskrivning;
    private String koordinater;
    private Boolean isDeleted = false;

    public enum Status {
        PRIVAT, PUBLIK
    }

    // Getters och setters
    // ...
}
