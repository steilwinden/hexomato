package de.siramac.hexomato.agent;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;

public interface Agent {
    Node getMove(Game game);
}
