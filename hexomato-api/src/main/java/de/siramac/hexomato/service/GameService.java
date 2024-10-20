package de.siramac.hexomato.service;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;
import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Long createGame(Player player, String name) {
        Game game = new Game(player, name);
        game = gameRepository.saveGame(game);
        return game.getId();
    }

    public List<Game> loadCurrentGames() {
        return gameRepository.loadCurrentGames();
    }

    public Game loadGame(Long gameId) {
        return gameRepository.loadGame(gameId);
    }

    public synchronized boolean joinGame(Long gameId, Player player, String name) {
        Game game = gameRepository.loadGame(gameId);
        if (game == null) {
            return false;
        }
        if (player == PLAYER_1) {
            game.setNamePlayer1(name);
        } else if (player == PLAYER_2) {
            game.setNamePlayer2(name);
        }
        gameRepository.saveGame(game);
        return true;
    }

    public Game makeMove(Long gameId, int row, int col, Player player) {
        Game game = gameRepository.loadGame(gameId);
        if (game == null) {
            return null;
        }
        Node node = game.getBoard()[row][col];
        if (!isValidMove(game, node, player)) {
            return null;
        }
        makeMoveOnBoard(game.getBoard(), node, player);

        Set<Node> winnerPath = findWinnerPath(game.getBoard(), node, player);
        if (!winnerPath.isEmpty()) {
            for (Node winnerNode : winnerPath) {
                winnerNode.setPartOfWinnerPath(true);
            }
            game.setWinner(player);
        } else {
            game.setTurn(game.getTurn() == PLAYER_1 ? PLAYER_2 : PLAYER_1);
        }
        game = gameRepository.saveGame(game);
        return game;
    }

    private void makeMoveOnBoard(Node[][] board, Node node, Player player) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setLastMove(false);
            }
        }
        node.setLastMove(true);
        node.setPlayer(player);
    }

    private boolean isValidMove(Game game, Node node, Player player) {
        return game.getWinner() == null && game.getTurn() == player && node.getPlayer() == null;
    }

    private Set<Node> findWinnerPath(Node[][] board, Node node, Player player) {
        Set<Node> visited = breadthFirstSearch(board, node);
        boolean containsRowMin = false;
        boolean containsRowMax = false;
        boolean containsColMin = false;
        boolean containsColMax = false;

        for (Node v : visited) {
            if (v.getRow() == 0) {
                containsRowMin = true;
            }
            if (v.getRow() == BOARD_SIZE - 1) {
                containsRowMax = true;
            }
            if (v.getCol() == 0) {
                containsColMin = true;
            }
            if (v.getCol() == BOARD_SIZE - 1) {
                containsColMax = true;
            }
        }
        if (player == PLAYER_1 && containsRowMin && containsRowMax) {
            return visited;
        } else if (player == PLAYER_2 && containsColMin && containsColMax) {
            return visited;
        }
        return Collections.emptySet();
    }


    private Set<Node> breadthFirstSearch(Node[][] board, Node start) {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : current.getNeighbours(board)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return visited;
    }

    public void deleteOlderGames() {
        gameRepository.deleteOlderGames();
    }
}
