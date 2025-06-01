package de.siramac.hexomato.agent.mcts;

import de.siramac.hexomato.domain.Game;
import de.siramac.hexomato.domain.Node;
import de.siramac.hexomato.domain.Player;
import de.siramac.hexomato.pattern.BridgePattern;
import lombok.Getter;

import java.util.*;

import static de.siramac.hexomato.agent.mcts.Util.getArgMax;
import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

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

    private final Random random;
    private final BridgePattern bridgePattern;

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
        this.children = new HashMap<>();
        this.childValues = new double[validActions.length];
        this.childVisits = new double[validActions.length];

        this.random = new Random();
        this.bridgePattern = new BridgePattern();
    }

    public double[] calculatePolicy() {
        double sum = Arrays.stream(childVisits).sum();
        return Arrays.stream(childVisits)
                .map(child -> child / sum)
                .toArray();
    }

    /**
     * UCB_i = exploitation + c * exploration
     * <p>
     * - exploitation = W_i / N_i (win rate)
     * - W_i: number of times child_i won
     * - N_i: number of times child_i visited
     * <p>
     * - exploration = EXPLORATION_COEFFICIENT * Math.sqrt(Math.log(N)) / N_i
     * - N: number of times parent visited
     * - EXPLORATION_COEFFICIENT: adjust the amount of exploration
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
            // end select stage if all moves are played or a winning state was found
            if (currentNode.validActions.length == 0 || currentNode.winner != null) {
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
        Node validAction = validActions[nextAction];
        simulationEnv.reset(state, activePlayer, winner);
        simulationEnv.makeMoveOnBoard(validAction.getRow(), validAction.getCol(), activePlayer);

        UCTNode child = new UCTNode(
                simulationEnv.getBoard(),
                simulationEnv.getTurn(),
                simulationEnv.getValidActions(),
                simulationEnv.getWinner(),
                nextAction,
                this);

        children.put(nextAction, child);
        return child;
    }

    /**
     * Game simulation until one player wins. To efficiently simulate the game:
     * - get all valid actions
     * - play bridge actions first
     * - play an available action if all bridge actions are exhausted
     * - alternate between players until all cells are occupied
     * After that, find a winning path from the first row of the board for PLAYER_1.
     * If a path was found, the winner is PLAYER_1, else PLAYER_2.
     */
    public Player simulate(Game simulationEnv) {
        simulationEnv.reset(state, activePlayer, null);

        List<Node> availableActions = new ArrayList<>(Arrays.asList(simulationEnv.getValidActions()));
        Collections.shuffle(availableActions);

        Set<Node> alreadyPlayedActions = new HashSet<>();
        Player player = activePlayer;
        for (Node availableAction : availableActions) {

            if (alreadyPlayedActions.contains(availableAction)) continue;

            int row = availableAction.getRow();
            int col = availableAction.getCol();
            Node node = simulationEnv.getBoard()[row][col];
            node.setPlayer(player);

            // check if current node is part of opponent bridge, then play the counter move
            Player opponent = player == PLAYER_1 ? PLAYER_2 : PLAYER_1;
            List<Node> possibleOpponentNodes = bridgePattern.getPossibleOpponentBridgeNodes(
                    opponent,
                    simulationEnv.getBoard(),
                    node);
            if (!possibleOpponentNodes.isEmpty()) {
                Node oppenentCounterNode = possibleOpponentNodes.get(random.nextInt(possibleOpponentNodes.size()));
                oppenentCounterNode.setPlayer(opponent);
                alreadyPlayedActions.add(oppenentCounterNode);
            } else {
                player = opponent;
            }
        }

        boolean isPlayer1Winner = Arrays.stream(simulationEnv.getBoard()[0])
                .filter(node -> node.getPlayer() == PLAYER_1)
                .map(node -> simulationEnv.findWinnerPath(simulationEnv.getBoard(), node, PLAYER_1))
                .anyMatch(winnerPath -> !winnerPath.isEmpty());

        return isPlayer1Winner ? PLAYER_1 : PLAYER_2;
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
