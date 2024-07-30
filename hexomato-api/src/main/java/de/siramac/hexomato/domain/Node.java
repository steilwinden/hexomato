package de.siramac.hexomato.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

}
