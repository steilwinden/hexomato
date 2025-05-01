package de.siramac.hexomato;

import de.siramac.hexomato.agent.Agent;
import de.siramac.hexomato.agent.mcts.MctsAgent;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MctsTest {

    @Test
    void mctsTest() {
        Game game = new Game(Player.PLAYER_1, false, "TestMcts");
        Agent agent = new MctsAgent(Player.PLAYER_2, new Game(Player.PLAYER_2, false, "TestMcts"));
        int row = 4;
        int col = 4;
        Node node = game.getBoard()[row][col];
        game.makeMoveOnBoard(node.getRow(), node.getCol(), PLAYER_1);

        Node aiMove = agent.getMove(game);
        assertThat(aiMove).isNotNull();
        assertThat(aiMove.getClass()).isEqualTo(Node.class);
    }
}
