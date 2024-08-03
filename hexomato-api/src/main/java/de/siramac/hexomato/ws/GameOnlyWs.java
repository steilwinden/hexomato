package de.siramac.hexomato.ws;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Player;
import lombok.Getter;

@Getter
public class GameOnlyWs {

    private final Long id;
    private final String namePlayer1;
    private final String namePlayer2;
    private final Player turn;
    private final Player winner;

    public GameOnlyWs(Game game) {
        this.id = game.getId();
        this.namePlayer1 = game.getNamePlayer1();
        this.namePlayer2 = game.getNamePlayer2();
        this.turn = game.getTurn();
        this.winner = game.getWinner();
    }
}
