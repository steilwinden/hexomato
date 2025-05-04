package de.siramac.hexomato.backend.entity;

import de.siramac.hexomato.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {

    List<GameEntity> findAllByCreatedOnAfter(Instant pointOfTime);

    void deleteAllByCreatedOnBefore(Instant pointOfTime);

    @Query("SELECT g.turn FROM GameEntity g WHERE g.id = :gameId")
    Player findTurnById(@Param("gameId") Long gameId);
}
