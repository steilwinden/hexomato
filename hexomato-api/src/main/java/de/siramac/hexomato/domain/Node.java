package de.siramac.hexomato.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Node {

    private int row;
    private int col;
    @Setter
    private boolean lastMove;
    @Setter
    private boolean partOfWinnerPath;
    @Setter
    private Player player;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

}
