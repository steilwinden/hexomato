package de.siramac.hexomato.domain;

public interface GameRepository {

    Game loadGame(Long id);

    Game saveGame(Game game);
}
