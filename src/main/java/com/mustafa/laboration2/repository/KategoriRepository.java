package com.mustafa.laboration2.repository;

import com.mustafa.laboration2.entity.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KategoriRepository extends JpaRepository<Kategori, Long> {
    boolean existsByNamn(String namn);
}
