package de.siramac.hexomato.backend.repository;

import de.siramac.hexomato.backend.entity.GameEntity;
import de.siramac.hexomato.backend.entity.NodeEntity;
import de.siramac.hexomato.backend.entity.NodeIdEntity;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;

@Component
public class GameMapper {

    public Game map(GameEntity gameEntity) {
        if (gameEntity == null) {
            return null;
        }

        Node[][] board = new Node[BOARD_SIZE][BOARD_SIZE];

        for (NodeEntity nodeEntity : gameEntity.getNodeEntityList()) {
            board[nodeEntity.getNodeIdEntity().getBoardRow()][nodeEntity.getNodeIdEntity().getBoardCol()] = createNode(nodeEntity);
        }

        return new Game(gameEntity.getId(), gameEntity.getNamePlayer1(), gameEntity.getNamePlayer2(),
                gameEntity.getTurn(), gameEntity.getWinner(), board);
    }

    private Node createNode(NodeEntity nodeEntity) {
        return new Node(nodeEntity.getNodeIdEntity().getBoardRow(), nodeEntity.getNodeIdEntity().getBoardCol(),
                nodeEntity.isLastMove(), nodeEntity.isPartOfWinnerPath(), nodeEntity.getPlayer());
    }

    public GameEntity map(Game game) {
        GameEntity gameEntity = createGameEntity(game);

        List<NodeEntity> nodeEntityList = new ArrayList<>();
        int maxRow = game.getBoard().length;
        int maxCol = game.getBoard()[0].length;
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                nodeEntityList.add(createNodeEntity(gameEntity, row, col, game.getBoard()[row][col]));
            }
        }

        gameEntity.setNodeEntityList(nodeEntityList);
        return gameEntity;
    }

    private static GameEntity createGameEntity(Game game) {
        GameEntity gameEntity = new GameEntity(game.getNamePlayer1(), game.getNamePlayer2(),
                game.getTurn(), game.getWinner(), null);
        gameEntity.setId(game.getId());
        return gameEntity;
    }

    private NodeEntity createNodeEntity(GameEntity gameEntity, int row, int col, Node node) {
        NodeIdEntity nodeIdEntity = new NodeIdEntity(gameEntity, row, col);
        return new NodeEntity(nodeIdEntity, node.isLastMove(), node.isPartOfWinnerPath(), node.getPlayer());
    }
}
