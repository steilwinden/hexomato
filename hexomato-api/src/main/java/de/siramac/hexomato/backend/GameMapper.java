package de.siramac.hexomato.backend;

import de.siramac.hexomato.backend.entity.GameEntity;
import de.siramac.hexomato.backend.entity.NodeEntity;
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
            board[nodeEntity.getBoardRow()][nodeEntity.getBoardCol()] = createNode(nodeEntity);
        }

        return new Game(gameEntity.getId(), gameEntity.getNamePlayer1(), gameEntity.getNamePlayer2(),
                gameEntity.getTurn(), gameEntity.getWinner(), gameEntity.getCreatedOn(), board);
    }

    private Node createNode(NodeEntity nodeEntity) {
        return new Node(nodeEntity.getId(), nodeEntity.getBoardRow(), nodeEntity.getBoardCol(),
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
                game.getTurn(), game.getWinner(), game.getCreatedOn(), null);
        gameEntity.setId(game.getId());
        return gameEntity;
    }

    private NodeEntity createNodeEntity(GameEntity gameEntity, int row, int col, Node node) {
        return new NodeEntity(row, col, gameEntity, node.isLastMove(), node.isPartOfWinnerPath(), node.getPlayer());
    }
}
