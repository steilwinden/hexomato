package de.siramac.hexomato.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game {

    @EqualsAndHashCode.Include
    private Long id;
    @Setter
    private String namePlayer1;
    @Setter
    private String namePlayer2;
    @Setter
    private Player turn;
    @Setter
    private Player winner;
    private Instant createdOn;
    private Node[][] board;

    public static final int BOARD_SIZE = 11;
    private static final int[][] neighbourMatrix = {
            {-1, 0, 0},
            {-1, 1, 1},
            {0, 0, -1},
            {0, 1, 1},
            {1, 0, -1},
            {1, 1, 0}
    };

    public Game(Player player, String name) {
        if (player == PLAYER_1) {
            namePlayer1 = name;
        } else if (player == PLAYER_2) {
            namePlayer2 = name;
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

    public List<Node> getNeighbours(Node node) {
        List<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int row = node.getRow() + i - 1;
            for (int j = 0; j < 2; j++) {
                int col = node.getCol() + neighbourMatrix[i][j];
                if (isValidNode(row, col)) {
                    neighbours.add(board[row][col]);
                }
            }
        }
        return neighbours;
    }

    private boolean isValidNode(int row, int col) {
        return row >= 0 && row < BOARD_SIZE
                && col >= 0 && col < BOARD_SIZE;
    }

}
