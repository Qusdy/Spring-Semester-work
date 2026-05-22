package ru.kpfu.itis.snetkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.snetkov.entity.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    
    Optional<Tag> findByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String name);
}