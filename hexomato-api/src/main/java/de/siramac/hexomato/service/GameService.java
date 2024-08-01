package de.siramac.hexomato.service;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.GameRepository;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public void createGame(Player player, String name) {
        Game game = new Game(player, name);
        gameRepository.saveGame(game);
    }

    public List<Game> loadCurrentGames() {
        return gameRepository.loadCurrentGames();
    }

    public synchronized boolean joinGame(Long gameId, String name) {
        Game game = gameRepository.loadGame(gameId);
        if (game == null) {
            return false;
        }
        if (game.getNamePlayer1() == null) {
            game.setNamePlayer1(name);
        } else if (game.getNamePlayer2() == null) {
            game.setNamePlayer2(name);
        }
        gameRepository.saveGame(game);
        return true;
    }

    public Game makeMove(Long gameId, int row, int col, Player player) {
        Game game = gameRepository.loadGame(gameId);
        if (!isValidMove(game.getBoard(), row, col)) {
            return null;
        }
        game.getBoard()[row][col].setPlayer(player);
        game = gameRepository.saveGame(game);
        return game;
    }

    private boolean isValidMove(Node[][] board, int row, int col) {
        return board[row][col].getPlayer() == null;
    }
}
