package de.siramac.hexomato.ws;

import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.Getter;

@Getter
public class NodeWs {

    private final boolean lastMove;
    private final boolean partOfWinnerPath;
    private final Player player;

    public NodeWs(Node node) {
        this.lastMove = node.isLastMove();
        this.partOfWinnerPath = node.isPartOfWinnerPath();
        this.player = node.getPlayer();
    }
}
