package de.siramac.hexomato.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

import static de.siramac.hexomato.domain.Player.PLAYER_1;
import static de.siramac.hexomato.domain.Player.PLAYER_2;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game {

    @EqualsAndHashCode.Include
    private Long id;
    @Setter
    private String namePlayer1;
    @Setter
    private boolean humanPlayer1;
    @Setter
    private String namePlayer2;
    @Setter
    private boolean humanPlayer2;
    @Setter
    private Player turn;
    @Setter
    private Player winner;
    private Instant createdOn;
    @Setter
    private Node[][] board;

    public static final int BOARD_SIZE = 11;

    public Game(Player player, boolean humanPlayer, String name) {
        if (player == PLAYER_1) {
            namePlayer1 = name;
            humanPlayer1 = humanPlayer;
        } else if (player == PLAYER_2) {
            namePlayer2 = name;
            humanPlayer2 = humanPlayer;
        }
        turn = PLAYER_1;
        createdOn = Instant.now();
        board = new Node[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new Node(row, col);
            }

        }
    }

    public int getNumActions() {
        return (int) Arrays.stream(board)
            .flatMap(Arrays::stream)
            .filter(node -> node.getPlayer() == null)
            .count();
    }

    public Node[] getValidActions() {
        return Arrays.stream(board)
            .flatMap(Arrays::stream)
            .filter(node -> node.getPlayer() == null)
            .toArray(Node[]::new);
    }

    public Node[][] getBoardCopy() {
        return deepCopyBoard(board);
    }

    public void reset(Node[][] board, Player player, Player winner) {
        this.board = deepCopyBoard(board);
        this.turn = player;
        this.winner = winner;
    }

    public void makeMoveOnBoard(int row, int col, Player player) {
        Node node = board[row][col];
        node.setPlayer(player);
        setNewGameState(node, player);
    }

    private void setNewGameState(Node node, Player player) {
        Set<Node> winnerPath = findWinnerPath(board, node, player);
        if (!winnerPath.isEmpty()) {
            for (Node winnerNode : winnerPath) {
                winnerNode.setPartOfWinnerPath(true);
            }
            setWinner(player);
        } else {
            setTurn(getTurn() == PLAYER_1 ? PLAYER_2 : PLAYER_1);
        }
    }

    private Set<Node> findWinnerPath(Node[][] board, Node node, Player player) {
        Set<Node> visited = breadthFirstSearch(board, node);
        boolean containsRowMin = false;
        boolean containsRowMax = false;
        boolean containsColMin = false;
        boolean containsColMax = false;

        for (Node v : visited) {
            if (v.getRow() == 0) {
                containsRowMin = true;
            }
            if (v.getRow() == BOARD_SIZE - 1) {
                containsRowMax = true;
            }
            if (v.getCol() == 0) {
                containsColMin = true;
            }
            if (v.getCol() == BOARD_SIZE - 1) {
                containsColMax = true;
            }
        }
        if (player == PLAYER_1 && containsRowMin && containsRowMax) {
            return visited;
        } else if (player == PLAYER_2 && containsColMin && containsColMax) {
            return visited;
        }
        return Collections.emptySet();
    }

    private Set<Node> breadthFirstSearch(Node[][] board, Node start) {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : current.getNeighbours(board)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return visited;
    }

    private Node[][] deepCopyBoard(Node[][] original) {
        int rows = original.length;
        int cols = original[0].length;
        Node[][] copy = new Node[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Node originalNode = original[i][j];
                Node copiedNode = new Node(
                        originalNode.getRow(),
                        originalNode.getCol()
                );
                copiedNode.setLastMove(originalNode.isLastMove());
                copiedNode.setPartOfWinnerPath(originalNode.isPartOfWinnerPath());
                copiedNode.setPlayer(originalNode.getPlayer());
                copy[i][j] = copiedNode;
            }
        }
        return copy;
    }
}
