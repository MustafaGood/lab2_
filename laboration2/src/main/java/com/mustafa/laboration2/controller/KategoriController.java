package com.mustafa.laboration2.controller;

import com.mustafa.laboration2.entity.Kategori;
import com.mustafa.laboration2.repository.KategoriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kategorier")
public class KategoriController {
    @Autowired
    private KategoriRepository kategoriRepository;

    @GetMapping
    public List<Kategori> getAll() {
        return kategoriRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kategori> getById(@PathVariable Long id) {
        Optional<Kategori> kategori = kategoriRepository.findById(id);
        return kategori.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Kategori> create(@RequestBody Kategori kategori) {
        if (kategoriRepository.existsByNamn(kategori.getNamn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Kategori saved = kategoriRepository.save(kategori);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
