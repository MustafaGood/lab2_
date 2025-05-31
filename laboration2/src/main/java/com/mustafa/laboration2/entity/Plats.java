package com.mustafa.laboration2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.Point;

// Denna klass representerar en plats i systemet
@Entity
@Table(name = "platser")
public class Plats {
    // Unikt ID för platsen
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Platsens namn
    @NotBlank(message = "Namn är obligatoriskt")
    @Size(min = 2, max = 100, message = "Namn måste vara mellan 2 och 100 tecken")
    @Column(nullable = false)
    private String namn;

    // Kategorin som platsen tillhör
    @NotNull(message = "Kategori är obligatorisk")
    @ManyToOne
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    // Användaren som skapade platsen
    @NotBlank(message = "Användar-ID är obligatoriskt")
    private String anvandarId;

    // Platsens status (privat eller publik)
    @NotNull(message = "Status är obligatorisk")
    @Enumerated(EnumType.STRING)
    private Status status = Status.PUBLIK;

    // När platsen skapades
    private LocalDateTime datumSkapad;
    // När platsen senast ändrades
    private LocalDateTime datumAndrad;

    // Beskrivning av platsen
    @Size(max = 1000, message = "Beskrivning får inte vara längre än 1000 tecken")
    private String beskrivning;

    // Platsens koordinater
    @Column(columnDefinition = "POINT SRID 4326")
    private Point koordinater;

    // Om platsen är borttagen eller inte
    private Boolean isDeleted = false;

    // Möjliga statusvärden för en plats
    public enum Status {
        PRIVAT, PUBLIK
    }

    // Getter och setter för ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter och setter för namn
    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    // Getter och setter för kategori
    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    // Getter och setter för användar-ID
    public String getAnvandarId() {
        return anvandarId;
    }

    public void setAnvandarId(String anvandarId) {
        this.anvandarId = anvandarId;
    }

    // Getter och setter för status
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Getter och setter för datum skapad
    public LocalDateTime getDatumSkapad() {
        return datumSkapad;
    }

    public void setDatumSkapad(LocalDateTime datumSkapad) {
        this.datumSkapad = datumSkapad;
    }

    // Getter och setter för datum ändrad
    public LocalDateTime getDatumAndrad() {
        return datumAndrad;
    }

    public void setDatumAndrad(LocalDateTime datumAndrad) {
        this.datumAndrad = datumAndrad;
    }

    // Getter och setter för beskrivning
    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    // Getter och setter för koordinater
    public Point getKoordinater() {
        return koordinater;
    }

    public void setKoordinater(Point koordinater) {
        this.koordinater = koordinater;
    }

    // Getter och setter för isDeleted
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
