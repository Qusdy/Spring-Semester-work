package ru.kpfu.itis.snetkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.snetkov.entity.NewsEntity;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
}