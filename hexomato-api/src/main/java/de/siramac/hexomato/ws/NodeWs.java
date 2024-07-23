package de.siramac.hexomato.ws;

import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.Getter;

@Getter
public class NodeWs {

    private final int row;
    private final int col;
    private final boolean lastMove;
    private final boolean partOfWinnerPath;
    private final Player player;

    public NodeWs(Node node) {
        this.row = node.getRow();
        this.col = node.getCol();
        this.lastMove = node.isLastMove();
        this.partOfWinnerPath = node.isPartOfWinnerPath();
        this.player = node.getPlayer();
    }
}
