package de.siramac.hexomato.backend;

import de.siramac.hexomato.backend.entity.GameEntity;
import de.siramac.hexomato.backend.entity.GameEntityRepository;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {

    private final GameEntityRepository gameEntityRepository;
    private final GameMapper gameMapper;
    private static final long TIMESPAN_IN_HOURS = 6;

    public Game loadGame(Long id) {
        GameEntity gameEntity = gameEntityRepository.findById(id).orElse(null);
        return gameMapper.map(gameEntity);
    }

    public List<Game> loadCurrentGames() {
        Instant pointInTime = Instant.now().minus(TIMESPAN_IN_HOURS, ChronoUnit.HOURS);
        List<GameEntity> gameEntityList = gameEntityRepository.findAllByCreatedOnAfter(pointInTime);
        return gameEntityList.stream().map(gameMapper::map).toList();
    }

    public Game saveGame(Game game) {
        GameEntity gameEntity = gameMapper.map(game);
        gameEntity = gameEntityRepository.save(gameEntity);
        return gameMapper.map(gameEntity);
    }

    public void deleteOlderGames() {
        Instant pointInTime = Instant.now().minus(TIMESPAN_IN_HOURS, ChronoUnit.HOURS);
        gameEntityRepository.deleteAllByCreatedOnBefore(pointInTime);
    }
}
