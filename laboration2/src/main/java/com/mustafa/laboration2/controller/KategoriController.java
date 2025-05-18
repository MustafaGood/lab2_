package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.entity.Kategori;
import com.mustafa.laboration2.repository.KategoriRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Denna klass hanterar alla förfrågningar som gäller kategorier
@RestController
@RequestMapping("/api/kategorier")
public class KategoriController {
    // Ansluter till databasen för kategorier
    @Autowired
    private KategoriRepository kategoriRepository;

    // Hämtar alla kategorier
    @GetMapping
    public ResponseEntity<List<Kategori>> getAll() {
        List<Kategori> kategorier = kategoriRepository.findAll();
        return ResponseEntity.ok(kategorier);
    }

    // Hämtar en specifik kategori baserat på ID
    @GetMapping("/{id}")
    public ResponseEntity<Kategori> getById(@PathVariable Long id) {
        Optional<Kategori> kategori = kategoriRepository.findById(id);
        return kategori.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Skapar en ny kategori (endast för administratörer)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Kategori kategori) {
        try {
            // Kontrollerar om kategorin redan finns
            if (kategoriRepository.existsByNamn(kategori.getNamn())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("En kategori med detta namn finns redan");
            }
            // Sparar den nya kategorin
            Kategori saved = kategoriRepository.save(kategori);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            // Hanterar eventuella fel som uppstår
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel uppstod vid skapande av kategori: " + e.getMessage());
        }
    }

    // Hanterar fel när någon skickar in felaktiga uppgifter
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
