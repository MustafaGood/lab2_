package com.mustafa.laboration2.repository;

import com.mustafa.laboration2.entity.Kategori;
import com.mustafa.laboration2.entity.Plats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PlatsRepository extends JpaRepository<Plats, Long> {
    List<Plats> findByStatusAndIsDeletedFalse(Plats.Status status);
    List<Plats> findByAnvandarIdAndIsDeletedFalse(String anvandarId);
    List<Plats> findByKategoriAndStatusAndIsDeletedFalse(Kategori kategori, Plats.Status status);

    @Query(value = "SELECT * FROM platser p WHERE ST_Distance_Sphere(p.koordinater, ST_GeomFromText(CONCAT('POINT(', :longitude, ' ', :latitude, ')'), 4326)) <= :radius AND p.is_deleted = false", nativeQuery = true)
    List<Plats> findWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radius") double radius);
}
