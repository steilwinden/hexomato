package de.siramac.hexomato.domain;

import lombok.*;

import java.time.Instant;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@ToString
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game {

    @EqualsAndHashCode.Include
    private Long id;
    @Setter
    private String namePlayer1;
    @Setter
    private boolean humanPlayer1;
    @Setter
    private String namePlayer2;
    @Setter
    private boolean humanPlayer2;
    @Setter
    private Player turn;
    @Setter
    private Player winner;
    private Instant createdOn;
    private Node[][] board;

    public static final int BOARD_SIZE = 11;

    public Game(Player player, boolean humanPlayer, String name) {
        if (player == PLAYER_1) {
            namePlayer1 = name;
            humanPlayer1 = humanPlayer;
        } else if (player == PLAYER_2) {
            namePlayer2 = name;
            humanPlayer2 = humanPlayer;
        }
        turn = PLAYER_1;
        createdOn = Instant.now();
        board = new Node[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new Node(row, col);
            }

        }
    }

}
