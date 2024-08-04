package de.siramac.hexomato.service;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        if (game.getTurn() != player) {
            return null;
        }
        if (!isValidMove(game.getBoard(), row, col)) {
            return null;
        }
        game.getBoard()[row][col].setPlayer(player);
        game.setTurn(game.getTurn() == PLAYER_1 ? PLAYER_2 : PLAYER_1);
        game = gameRepository.saveGame(game);
        return game;
    }

    private boolean isValidMove(Node[][] board, int row, int col) {
        return board[row][col].getPlayer() == null;
    }
}
