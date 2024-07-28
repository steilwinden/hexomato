package de.siramac.hexomato.backend;

import de.siramac.hexomato.backend.entity.GameEntity;
import de.siramac.hexomato.backend.entity.GameEntityRepository;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {

    private final GameEntityRepository gameEntityRepository;
    private final GameMapper gameMapper;

    public Game loadGame(Long id) {
        GameEntity gameEntity = gameEntityRepository.findById(id).orElse(null);
        return gameMapper.map(gameEntity);


    }

    public Game saveGame(Game game) {
        GameEntity gameEntity = gameMapper.map(game);
        gameEntity = gameEntityRepository.save(gameEntity);
        return gameMapper.map(gameEntity);
    }
}
