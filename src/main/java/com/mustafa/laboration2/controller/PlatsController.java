package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.entity.Plats;
import com.mustafa.laboration2.repository.PlatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/platser")
public class PlatsController {
    @Autowired
    private PlatsRepository platsRepository;

    @GetMapping
    public List<Plats> getPublika() {
        return platsRepository.findByStatusAndIsDeletedFalse(Plats.Status.PUBLIK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plats> getPublikPlats(@PathVariable Long id) {
        Optional<Plats> plats = platsRepository.findById(id)
            .filter(p -> p.getStatus() == Plats.Status.PUBLIK && !p.getIsDeleted());
        return plats.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/mina")
    public List<Plats> getMinaPlatser(@AuthenticationPrincipal UserDetails user) {
        return platsRepository.findByAnvandarIdAndIsDeletedFalse(user.getUsername());
    }

    @PostMapping
    public ResponseEntity<Plats> create(@RequestBody Plats plats, @AuthenticationPrincipal UserDetails user) {
        plats.setAnvandarId(user.getUsername());
        plats.setDatumSkapad(LocalDateTime.now());
        plats.setDatumAndrad(LocalDateTime.now());
        plats.setStatus(plats.getStatus() == null ? Plats.Status.PUBLIK : plats.getStatus());
        return ResponseEntity.ok(platsRepository.save(plats));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plats> update(@PathVariable Long id, @RequestBody Plats plats, @AuthenticationPrincipal UserDetails user) {
        Optional<Plats> optionalPlats = platsRepository.findById(id);
        if (optionalPlats.isEmpty()) return ResponseEntity.notFound().build();
        Plats existing = optionalPlats.get();
        if (!existing.getAnvandarId().equals(user.getUsername())) return ResponseEntity.status(403).build();
        existing.setNamn(plats.getNamn());
        existing.setKategori(plats.getKategori());
        existing.setStatus(plats.getStatus());
        existing.setDatumAndrad(LocalDateTime.now());
        existing.setBeskrivning(plats.getBeskrivning());
        existing.setKoordinater(plats.getKoordinater());
        return ResponseEntity.ok(platsRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        Optional<Plats> optionalPlats = platsRepository.findById(id);
        if (optionalPlats.isEmpty()) return ResponseEntity.notFound().build();
        Plats plats = optionalPlats.get();
        if (!plats.getAnvandarId().equals(user.getUsername())) return ResponseEntity.status(403).build();
        plats.setIsDeleted(true);
        plats.setDatumAndrad(LocalDateTime.now());
        platsRepository.save(plats);
        return ResponseEntity.noContent().build();
    }
}
