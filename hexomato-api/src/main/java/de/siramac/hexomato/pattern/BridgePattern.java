package de.siramac.hexomato.pattern;

import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;

import java.util.ArrayList;
import java.util.List;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;

public class BridgePattern {

    private final Node[][] bridgePatterns = {
            {new Node(-1, 1), new Node(1, 0), new Node(0, 1)},
            {new Node(-1, 0), new Node(1, -1), new Node(0, -1)},
            {new Node(0, 1), new Node(1, -1), new Node(1, 0)},
            {new Node(-1, 1), new Node(0, -1), new Node(-1, 0)},
            {new Node(0, -1), new Node(1, 0), new Node(1, -1)},
            {new Node(-1, -0), new Node(0, 1), new Node(-1, 1)},
    };

    public List<Node> getPossibleOpponentBridgeNodes(Player opponent, Node[][] board, Node node) {
        List<Node> possibleOpponentNodes = new ArrayList<>();
        for (Node[] bridgePattern : bridgePatterns) {

            if (!isValidBridgeAction(node, bridgePattern)) continue;

            Node opponentBridge1 = board[node.getRow() + bridgePattern[0].getRow()][node.getCol() + bridgePattern[0].getCol()];
            Node opponentBridge2 = board[node.getRow() + bridgePattern[1].getRow()][node.getCol() + bridgePattern[1].getCol()];
            Node opponentBridge3 = board[node.getRow() + bridgePattern[2].getRow()][node.getCol() + bridgePattern[2].getCol()]; // empty one

            if (opponentBridge1.getPlayer() == opponent &&
                opponentBridge2.getPlayer() == opponent &&
                opponentBridge3.getPlayer() == null) {
                possibleOpponentNodes.add(opponentBridge3);
            }
        }

        return possibleOpponentNodes;
    }

    private boolean isValidBridgeAction(Node node, Node[] bridgePattern) {
        for (Node bridgeNode : bridgePattern) {
            int row = node.getRow() + bridgeNode.getRow();
            int col = node.getCol() + bridgeNode.getCol();
            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                return false;
            }
        }
        return true;
    }
}
