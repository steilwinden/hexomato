package de.siramac.hexomato;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static de.siramac.hexomato.domain.Game.BOARD_SIZE;

@SpringBootTest
public class GameTest {
    private static Game game;

    @BeforeAll
    static void setup() {
        game = new Game(PLAYER_1, true,"Test Player");
    }

    @Test
    void makeMoveOnBoardTest() {
        // Arrange
        int row = 4;
        int col = 4;
        Node node = game.getBoard()[row][col];

        // Act
        game.makeMoveOnBoard(node.getRow(), node.getCol(), PLAYER_1);

        // Assert
        assertThat(game.getBoard()[row][col].getPlayer()).isEqualTo(PLAYER_1);
        assertThat(game.getTurn()).isEqualTo(PLAYER_2);
    }

    @Test
    void getNumActionsTest() {
        // Act
        int numActions = game.getNumActions();

        // Assert
        assertThat(numActions).isEqualTo(BOARD_SIZE * BOARD_SIZE - 1);
    }


    @Test
    void resetTest() {
        // Arrange
        game = new Game(PLAYER_1, true,"Test Player");
        Node[][] board = game.getBoardCopy();
        Player activePlayer = game.getTurn();
        Player winner = game.getWinner();
        assertThat(activePlayer).isEqualTo(PLAYER_1);

        // mutate the game state
        Node node = board[5][5];
        game.makeMoveOnBoard(node.getRow(), node.getCol(), activePlayer);
        game.setWinner(PLAYER_2);

        // assertions before reset
        assertThat(game.getTurn()).isNotEqualTo(activePlayer);
        assertThat(game.getWinner()).isEqualTo(PLAYER_2);
        assertThat(game.getBoard()[5][5].getPlayer()).isEqualTo(PLAYER_1);

        // reset to original state
        game.reset(board, activePlayer, winner);
        assertThat(board[5][5].getPlayer()).isNull();

        // assertions after reset
        assertThat(game.getTurn()).isEqualTo(PLAYER_1);
        assertThat(game.getWinner()).isEqualTo(null);
        assertThat(game.getBoard()[5][5].getPlayer()).isNull();
    }
}
