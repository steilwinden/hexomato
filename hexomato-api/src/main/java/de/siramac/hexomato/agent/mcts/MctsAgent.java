package de.siramac.hexomato.agent.mcts;

import de.siramac.hexomato.agent.Agent;
import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static de.siramac.hexomato.agent.mcts.Util.getArgMax;

@Slf4j
@AllArgsConstructor
public class MctsAgent implements Agent {

    private Player player;
    private Game simulationEnv;
    final long SIMULATION_TIME = 2_000; // in milliseconds

    @Override
    public Node getMove(Game game) {
        simulationEnv.reset(game.getBoard(), player, game.getWinner()); // update simulation environment to current env
        UCTNode rootNode = monteCarloTreeSearch();
        double[] policy = rootNode.calculatePolicy();
        int argmax = getArgMax(policy);
        return game.getValidActions()[argmax];
    }

    private UCTNode monteCarloTreeSearch() {
        long startTime = System.currentTimeMillis();
        UCTNode rootNode = new UCTNode(
                simulationEnv.getBoard(),
                simulationEnv.getTurn(),
                simulationEnv.getValidActions(),
                simulationEnv.getWinner(),
                null,
                null
        );
        int numSimulations = 0;
        while (System.currentTimeMillis() - startTime < SIMULATION_TIME) {

            // Selection
            SelectionResult selectionResult = rootNode.select();
            UCTNode selectedNode = selectionResult.node();
            Integer nextAction = selectionResult.action();

            // Expansion
            UCTNode leafNode = selectedNode.expand(simulationEnv, nextAction);
            Player winner = selectedNode.getWinner();

            // Simulation
            if (winner == null) {
                winner = leafNode.simulate(simulationEnv);
            }

            // Backup
            leafNode.backup(winner);
            numSimulations++;
        }
        log.info("Number of simulations: {}", numSimulations);
        return rootNode;
    }
}
