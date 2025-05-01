package de.siramac.hexomato.agent.random;

import de.siramac.hexomato.agent.Agent;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;

import java.util.Random;

/**
 * This Agent just returns a random action
 */
public class RandomAgent implements Agent {

    @Override
    public Node getMove(Game game) {
        Node[] availableActions = game.getValidActions();
        return availableActions[new Random().nextInt(availableActions.length)];
    }
}
