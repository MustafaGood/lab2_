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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public String getAnvandarId() {
        return anvandarId;
    }

    public void setAnvandarId(String anvandarId) {
        this.anvandarId = anvandarId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDatumSkapad() {
        return datumSkapad;
    }

    public void setDatumSkapad(LocalDateTime datumSkapad) {
        this.datumSkapad = datumSkapad;
    }

    public LocalDateTime getDatumAndrad() {
        return datumAndrad;
    }

    public void setDatumAndrad(LocalDateTime datumAndrad) {
        this.datumAndrad = datumAndrad;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public String getKoordinater() {
        return koordinater;
    }

    public void setKoordinater(String koordinater) {
        this.koordinater = koordinater;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
