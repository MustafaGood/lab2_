package com.mustafa.laboration2.repository;

import com.mustafa.laboration2.entity.Plats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlatsRepository extends JpaRepository<Plats, Long> {
    List<Plats> findByStatusAndIsDeletedFalse(Plats.Status status);
    List<Plats> findByAnvandarIdAndIsDeletedFalse(String anvandarId);
    List<Plats> findByIdAndStatusAndIsDeletedFalse(Long id, Plats.Status status);
}
