package de.siramac.hexomato.service;

import de.siramac.hexomato.ai.AiAssistant;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;
import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final AiAssistant aiAssistant;

    public Long createGame(Player player, boolean humanPlayer, String name) {
        Game game = new Game(player, humanPlayer, name);
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
            game.setHumanPlayer1(true);
        } else if (player == PLAYER_2) {
            game.setNamePlayer2(name);
            game.setHumanPlayer2(true);
        }
        gameRepository.saveGame(game);

        if (!game.isHumanPlayer1()) {
            createGame(PLAYER_1, false, "WALL-E");
        }
        if (!game.isHumanPlayer2()) {
            createGame(PLAYER_2, false, "EVE");
        }
        return true;
    }

    public Game makeMove(Long gameId, int row, int col, Player player) {
        Game game = gameRepository.loadGame(gameId);
        if (!isValidGameState(game)) {
            log.error("Invalid game state. game: {}", game);
            return null;
        }
        if (!isValidRowAndColumn(row, col)) {
            log.error("Invalid row or column. row: {}, column: {}", row, col);
            return null;
        }
        Node node = game.getBoard()[row][col];
        if (!isValidMove(game, node, player)) {
            log.error("Invalid move. turn: {}, player: {}", game.getTurn(), node.getPlayer());
            return null;
        }
        makeMoveOnBoard(game.getBoard(), node, player);

        setNewGameState(game, node, player);
        game = gameRepository.saveGame(game);
        return game;
    }

    private boolean isValidGameState(Game game) {
        return game != null && game.getWinner() == null;
    }

    private boolean isValidRowAndColumn(int row, int col) {
        return col >= 0 && col < BOARD_SIZE && row >= 0 & row < BOARD_SIZE;
    }

    private boolean isValidMove(Game game, Node node, Player player) {
        return game.getTurn() == player && node.getPlayer() == null;
    }

    private void setNewGameState(Game game, Node node, Player player) {
        Set<Node> winnerPath = findWinnerPath(game.getBoard(), node, player);
        if (!winnerPath.isEmpty()) {
            for (Node winnerNode : winnerPath) {
                winnerNode.setPartOfWinnerPath(true);
            }
            game.setWinner(player);
        } else {
            game.setTurn(game.getTurn() == PLAYER_1 ? PLAYER_2 : PLAYER_1);
        }
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

    public boolean isAiTurn(Game game) {
        return isValidGameState(game) &&
                ((game.getTurn() == PLAYER_1 && !game.isHumanPlayer1())
                        || (game.getTurn() == PLAYER_2 && !game.isHumanPlayer2()));
    }

    public Game makeAiMove(Game game) {
        Node lastMove = getLastMove(game);
        StringBuilder userMessageContent = new StringBuilder("We are playing on gameId " + game.getId() + ". ");
        if (lastMove == null) {
            userMessageContent.append("The game starts. ");
        } else {
            userMessageContent.append("I make this move: row ").append(lastMove.getRow()).append(", column ").append(lastMove.getCol()).append(". ");
        }
        userMessageContent.append("It's your turn now, ").append(game.getTurn()).append(".");
        log.info("User: {}", userMessageContent);
        String response = retryWithTimeout(
                () -> aiAssistant.chat(game.getId(), userMessageContent.toString())
                        .collectList()
                        .map(s -> String.join(" ", s))
                        .block(),
                () -> game.getTurn() == gameRepository.loadTurn(game.getId())
        );
        log.info("AI: {}", response);
        return loadGame(game.getId());
    }

    private Node getLastMove(Game game) {
        Node lastMove = null;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (game.getBoard()[i][j].isLastMove()) {
                    lastMove = game.getBoard()[i][j];
                }
            }
        }
        return lastMove;
    }

    private String retryWithTimeout(Callable<String> method, Callable<Boolean> condition) {
        final int maxAttempts = 5;
        final long timeoutMillis = 3000;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (condition.call()) {
                    // condition.call() checks if a previous method.call() already set a move,
                    // although an exception was thrown
                    return method.call();
                }
            } catch (Exception e) {
                log.error("Attempt {} failed: {}", attempt, e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(timeoutMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Retry process was interrupted: ", ie);
                    }
                }
            }
        }
        return null;
    }

    public void deleteOlderGames() {
        gameRepository.deleteOlderGames();
    }
}
