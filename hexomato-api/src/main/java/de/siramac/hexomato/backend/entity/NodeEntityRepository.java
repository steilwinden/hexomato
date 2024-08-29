package de.siramac.hexomato.backend.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface NodeEntityRepository extends JpaRepository<GameEntity, Long> {

    void deleteAllByCreatedOnBefore(Instant pointOfTime);
}