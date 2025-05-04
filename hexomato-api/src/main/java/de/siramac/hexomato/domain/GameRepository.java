package de.siramac.hexomato.domain;

import java.util.List;

public interface GameRepository {

    Game loadGame(Long id);

    Game saveGame(Game game);

    List<Game> loadCurrentGames();

    void deleteOlderGames();

}
