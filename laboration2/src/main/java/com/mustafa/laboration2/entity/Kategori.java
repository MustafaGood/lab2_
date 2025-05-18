package com.mustafa.laboration2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Denna klass representerar en kategori i systemet
@Entity
@Table(name = "kategorier", uniqueConstraints = @UniqueConstraint(columnNames = "namn"))
public class Kategori {
    // Unikt ID för kategorin
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kategorins namn (måste vara unikt)
    @NotBlank(message = "Namn är obligatoriskt")
    @Size(min = 2, max = 50, message = "Namn måste vara mellan 2 och 50 tecken")
    @Column(nullable = false, unique = true)
    private String namn;

    // En symbol som kan användas för att representera kategorin
    @Size(max = 50, message = "Symbol får inte vara längre än 50 tecken")
    private String symbol;

    // En beskrivning av kategorin
    @Size(max = 500, message = "Beskrivning får inte vara längre än 500 tecken")
    private String beskrivning;

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

    // Getter och setter för symbol
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    // Getter och setter för beskrivning
    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }
}
