package de.siramac.hexomato.agent.mcts;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static de.siramac.hexomato.agent.mcts.Util.getArgMax;

public class UCTNode {
    private final Node[][] state;
    private final Player activePlayer;
    private final Integer action; // index to availableActions
    private final UCTNode parent;
    private final Map<Integer, UCTNode> children;
    private final Node[] validActions;
    private final double[] childValues;
    private final double[] childVisits;
    private final double EXPLORATION_COEFFICIENT = Math.sqrt(2);
    @Getter
    private final Player winner;

    public UCTNode(
            Node[][] state,
            Player activePlayer,
            Node[] validActions,
            Player winner,
            Integer action,
            UCTNode parent
    ) {
        this.state = state;
        this.activePlayer = activePlayer;
        this.validActions = validActions;
        this.winner = winner;
        this.action = action;
        this.parent = parent;
        this.children = new HashMap<Integer, UCTNode>();
        this.childValues = new double[validActions.length];
        this.childVisits = new double[validActions.length];
    }

    public double[] calculatePolicy() {
        double sum = Arrays.stream(childVisits).sum();
        return Arrays.stream(childVisits)
                .map(child -> child / sum)
                .toArray();
    }

    /**
        UCB_i = exploitation + c * exploration

        - exploitation = W_i / N_i (winrate)
        - W_i: number of times child_i won
        - N_i: number of times child_i visited

        - exploration = EXPLORATION_COEFFICIENT * Math.sqrt(Math.log(N)) / N_i
        - N: number of times parent visited
        - EXPLORATION_COEFFICIENT: adjust the amount of exploration
     */
    public double[] calculateUpperConfidenceBoundForTrees() {
        double[] UCB = new double[validActions.length];
        double N = Arrays.stream(childVisits).sum();

        for (int i = 0; i < validActions.length; i++) {
            double W_i = childValues[i];
            double N_i = childVisits[i];

            if (N_i == 0.0) {
                UCB[i] = Double.POSITIVE_INFINITY;
                continue;
            }

            // exploitation
            double exploitation = W_i / N_i;

            // exploration
            double exploration = EXPLORATION_COEFFICIENT * (Math.sqrt(Math.log(N)) / N_i);

            // sum
            UCB[i] = exploitation + exploration;

        }
        return UCB;
    }

    public SelectionResult select() {
        UCTNode currentNode = this;
        Integer bestAction;

        while (true) {
            if (currentNode.validActions.length == 0) {
                bestAction = null;
                break;
            }

            double[] uctValues = currentNode.calculateUpperConfidenceBoundForTrees();
            bestAction = getArgMax(uctValues);

            if (currentNode.children.containsKey(bestAction)) {
                currentNode = currentNode.children.get(bestAction);
            } else {
                break;
            }
        }
        return new SelectionResult(currentNode, bestAction);
    }

    public UCTNode expand(Game simulationEnv, Integer nextAction) {
        simulationEnv.reset(state, activePlayer, winner);
        UCTNode child = this;

        if (winner == null) {
            Node validAction = validActions[nextAction];
            simulationEnv.makeMoveOnBoard(validAction.getRow(), validAction.getCol(), activePlayer);
            child = new UCTNode(
                    simulationEnv.getBoard(),
                    simulationEnv.getTurn(),
                    simulationEnv.getValidActions(),
                    simulationEnv.getWinner(),
                    nextAction,
                    this
            );
            children.put(nextAction, child);
        }
        return child;
    }

    public Player simulate(Game simulationEnv) {
        simulationEnv.reset(state, activePlayer, winner);
        Player simulatedWinner = winner;

        while (simulatedWinner == null) {

            Node[] availableActions = simulationEnv.getValidActions();
            Node randomAction = availableActions[new Random().nextInt(availableActions.length)];
            simulationEnv.makeMoveOnBoard(
                    randomAction.getRow(),
                    randomAction.getCol(),
                    simulationEnv.getTurn()
            );

            simulatedWinner = simulationEnv.getWinner();
        }
        return simulatedWinner;
    }

    public void backup(Player winner) {
        UCTNode currentNode = this;

        while (true) {
            Integer currentAction = currentNode.action;
            currentNode = currentNode.parent;

            if (currentNode == null) {
                break;
            }

            if (currentNode.activePlayer == winner) {
                currentNode.childValues[currentAction]++;
            }

            currentNode.childVisits[currentAction]++;
        }
    }

}
