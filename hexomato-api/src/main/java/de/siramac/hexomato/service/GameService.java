package de.siramac.hexomato.service;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        if (!isValidMove(game, row, col, player)) {
            return null;
        }
        setMoveOnBoard(game.getBoard(), row, col, player);

        game.setTurn(game.getTurn() == PLAYER_1 ? PLAYER_2 : PLAYER_1);
        game = gameRepository.saveGame(game);
        return game;
    }

    private static void setMoveOnBoard(Node[][] board, int row, int col, Player player) {
        board[row][col].setPlayer(player);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setLastMove(false);
            }
        }
        board[row][col].setLastMove(true);
    }

    private boolean isValidMove(Game game, int row, int col, Player player) {
        return game.getTurn() == player && game.getBoard()[row][col].getPlayer() == null;
    }
}
