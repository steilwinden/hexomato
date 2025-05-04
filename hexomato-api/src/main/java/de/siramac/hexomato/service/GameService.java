package de.siramac.hexomato.service;

import de.siramac.hexomato.agent.Agent;
import de.siramac.hexomato.agent.mcts.MctsAgent;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.siramac.hexomato.domain.Game.BOARD_SIZE;
import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    private Agent agent;

    public Long createGame(Player player, boolean humanPlayer, String name) {
        if (!humanPlayer) {
            agent = new MctsAgent(player, new Game(player, false, name));
        }

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
            createGame(PLAYER_1, false, "αMax");
        }
        if (!game.isHumanPlayer2()) {
            createGame(PLAYER_2, false, "Monte-Carlo");
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

        game.makeMoveOnBoard(node.getRow(), node.getCol(), player);
        game = gameRepository.saveGame(game);
        return game;
    }

    public void deleteOlderGames() {
        gameRepository.deleteOlderGames();
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

    public boolean isAiTurn(Game game) {
        return isValidGameState(game) &&
            (
                (game.getTurn() == PLAYER_1 && !game.isHumanPlayer1()) ||
                (game.getTurn() == PLAYER_2 && !game.isHumanPlayer2())
            );
    }

    public Game makeAiMove(Game game) {
        Node node = agent.getMove(game);
        game = makeMove(game.getId(), node.getRow(), node.getCol(), game.getTurn());
        return game;
    }
}
