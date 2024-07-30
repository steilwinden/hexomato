package de.siramac.hexomato.backend.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {

    List<GameEntity> findAllByCreatedOnAfter(Instant createdOn);
}
