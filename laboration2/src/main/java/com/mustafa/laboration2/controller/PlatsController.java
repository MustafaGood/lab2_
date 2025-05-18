package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.entity.Plats;
import com.mustafa.laboration2.repository.PlatsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Denna klass hanterar alla förfrågningar som gäller platser
@RestController
@RequestMapping("/api/platser")
public class PlatsController {
    // Ansluter till databasen för platser
    @Autowired
    private PlatsRepository platsRepository;

    // Hämtar alla publika platser som inte är borttagna
    @GetMapping
    public ResponseEntity<List<Plats>> getPublika() {
        List<Plats> platser = platsRepository.findByStatusAndIsDeletedFalse(Plats.Status.PUBLIK);
        return ResponseEntity.ok(platser);
    }

    // Hämtar en specifik publik plats baserat på ID
    @GetMapping("/{id}")
    public ResponseEntity<Plats> getPublikPlats(@PathVariable Long id) {
        Optional<Plats> plats = platsRepository.findById(id)
            .filter(p -> p.getStatus() == Plats.Status.PUBLIK && !p.getIsDeleted());
        return plats.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Hämtar alla platser som tillhör den inloggade användaren
    @GetMapping("/mina")
    public ResponseEntity<List<Plats>> getMinaPlatser(@AuthenticationPrincipal UserDetails user) {
        List<Plats> platser = platsRepository.findByAnvandarIdAndIsDeletedFalse(user.getUsername());
        return ResponseEntity.ok(platser);
    }

    // Skapar en ny plats
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Plats plats, @AuthenticationPrincipal UserDetails user) {
        try {
            // Sätter användar-ID och datum för den nya platsen
            plats.setAnvandarId(user.getUsername());
            plats.setDatumSkapad(LocalDateTime.now());
            plats.setDatumAndrad(LocalDateTime.now());
            plats.setStatus(plats.getStatus() == null ? Plats.Status.PUBLIK : plats.getStatus());
            Plats saved = platsRepository.save(plats);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel uppstod vid skapande av plats: " + e.getMessage());
        }
    }

    // Uppdaterar en befintlig plats
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Plats plats, 
            @AuthenticationPrincipal UserDetails user) {
        try {
            // Kontrollerar om platsen finns
            Optional<Plats> optionalPlats = platsRepository.findById(id);
            if (optionalPlats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Platsen kunde inte hittas");
            }
            
            // Kontrollerar om användaren har rätt att uppdatera platsen
            Plats existing = optionalPlats.get();
            if (!existing.getAnvandarId().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Du har inte behörighet att uppdatera denna plats");
            }

            // Uppdaterar platsens information
            existing.setNamn(plats.getNamn());
            existing.setKategori(plats.getKategori());
            existing.setStatus(plats.getStatus());
            existing.setDatumAndrad(LocalDateTime.now());
            existing.setBeskrivning(plats.getBeskrivning());
            existing.setKoordinater(plats.getKoordinater());
            
            Plats updated = platsRepository.save(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel uppstod vid uppdatering av plats: " + e.getMessage());
        }
    }

    // Tar bort en plats (markerar den som borttagen)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        try {
            // Kontrollerar om platsen finns
            Optional<Plats> optionalPlats = platsRepository.findById(id);
            if (optionalPlats.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Platsen kunde inte hittas");
            }
            
            // Kontrollerar om användaren har rätt att ta bort platsen
            Plats plats = optionalPlats.get();
            if (!plats.getAnvandarId().equals(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Du har inte behörighet att ta bort denna plats");
            }

            // Markerar platsen som borttagen
            plats.setIsDeleted(true);
            plats.setDatumAndrad(LocalDateTime.now());
            platsRepository.save(plats);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel uppstod vid borttagning av plats: " + e.getMessage());
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
