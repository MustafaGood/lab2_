package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.dto.CreatePlatsRequest;
import com.mustafa.laboration2.dto.PlatsResponse;
import com.mustafa.laboration2.entity.Kategori;
import com.mustafa.laboration2.entity.Plats;
import com.mustafa.laboration2.repository.KategoriRepository;
import com.mustafa.laboration2.repository.PlatsRepository;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import java.util.stream.Collectors;

// Denna klass hanterar alla förfrågningar som gäller platser
@RestController
@RequestMapping("/api/platser")
public class PlatsController {
    // Ansluter till databasen för platser
    @Autowired
    private PlatsRepository platsRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

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

    @GetMapping("/category/{kategoriId}")
    public ResponseEntity<List<Plats>> getPublikaByKategori(@PathVariable Long kategoriId) {
        Optional<Kategori> kategori = kategoriRepository.findById(kategoriId);
        if (kategori.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Plats> platser = platsRepository.findByKategoriAndStatusAndIsDeletedFalse(kategori.get(),
                Plats.Status.PUBLIK);
        return ResponseEntity.ok(platser);
    }

    // Hämtar alla platser som tillhör den inloggade användaren
    @GetMapping("/mina")
    public ResponseEntity<List<Plats>> getMinaPlatser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        List<Plats> platser = platsRepository.findByAnvandarIdAndIsDeletedFalse(username);
        return ResponseEntity.ok(platser);
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5000") double radius,
            @AuthenticationPrincipal UserDetails user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "Unauthorized",
                            "message", "Authentication required"));
        }

        try {
            List<Plats> platser = platsRepository.findWithinRadius(longitude, latitude, radius);
            List<PlatsResponse> response = platser.stream()
                    .map(PlatsResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()));
        }
    }

    // Skapar en ny plats
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreatePlatsRequest request,
            Authentication authentication) {
        try {
            Optional<Kategori> kategori = kategoriRepository.findById(request.getKategoriId());
            if (kategori.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Kategorin kunde inte hittas");
            }

            Point koordinater = geometryFactory.createPoint(
                    new Coordinate(request.getLongitude(), request.getLatitude()));
            koordinater.setSRID(4326);

            Plats plats = new Plats();
            plats.setNamn(request.getNamn());
            plats.setKategori(kategori.get());
            plats.setBeskrivning(request.getBeskrivning());
            plats.setKoordinater(koordinater);
            plats.setAnvandarId(authentication.getName());
            plats.setDatumSkapad(LocalDateTime.now());
            plats.setDatumAndrad(LocalDateTime.now());
            plats.setStatus(Plats.Status.PUBLIK);

            Plats saved = platsRepository.save(plats);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PlatsResponse(saved));
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
