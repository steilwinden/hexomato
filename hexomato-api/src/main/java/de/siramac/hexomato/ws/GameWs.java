package de.siramac.hexomato.ws;


import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Player;
import lombok.Getter;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;

@Getter
public class GameWs {

    private final Long id;
    private final String namePlayer1;
    private final String namePlayer2;
    private final Player turn;
    private final Player winner;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final NodeWs[][] board;

    public GameWs(Game game) {
        this.id = game.getId();
        this.namePlayer1 = game.getNamePlayer1();
        this.namePlayer2 = game.getNamePlayer2();
        this.turn = game.getTurn();
        this.winner = game.getWinner();
        board = new NodeWs[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new NodeWs(game.getBoard()[row][col]);
            }
        }

    }
}
