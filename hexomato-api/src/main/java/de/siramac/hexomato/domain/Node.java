package de.siramac.hexomato.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node {

    private Long id;
    @EqualsAndHashCode.Include
    private int row;
    @EqualsAndHashCode.Include
    private int col;
    @Setter
    private boolean lastMove;
    @Setter
    private boolean partOfWinnerPath;
    @Setter
    private Player player;

    public static final int[][] neighbourMatrix = {
            {0, 1},   // n[0][0]= 0, n[0][1]=1
            {-1, 1},  // n[1][0]=-1, n[1][1]=1
            {-1, 0}   // n[2][0]=-1, n[2][1]=0
    };

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public List<Node> getNeighbours(Node[][] board) {
        List<Node> neighbours = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int r = row + i - 1;
            for (int j = 0; j < 2; j++) {
                int c = col + neighbourMatrix[i][j];
                if (isValidNode(r, c) && board[r][c].getPlayer() == player) {
                    neighbours.add(board[r][c]);
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
