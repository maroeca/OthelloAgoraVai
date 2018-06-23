package maycowMyllaPlayer;

import game.AbstractPlayer;
import game.BoardSquare;
import game.Move;
import game.OthelloGame;

import java.util.ArrayList;
import java.util.List;

public class BFSPlayer extends AbstractPlayer {
    public BoardSquare play(int[][] tab) {
        OthelloGame game = new OthelloGame();

        Move bestMove = breadthFirstSearch(tab, 5);
        return bestMove.getBardPlace();
    }

    private Move breadthFirstSearch(int[][] tab, int maxDepth) {
        Graph graph = new Graph();
        graph.setupGraph(tab, getMyBoardMark(), maxDepth);

        Node node = miniMax(graph.getStartNode(), 0, maxDepth);
        return node.getMove();
    }

    private Node miniMax(Node node, int depth, int maxDepth) {
        Node n = node;

        if (depth == maxDepth || !node.hasChildren())
            return node;
        else {
            if (node.getPlayer() == getMyBoardMark()) {
                for(Node child : node.getChildren()) {
                    n = max(child, miniMax(child, depth + 1, maxDepth));
                }

            } else {
                for(Node child : node.getChildren()) {
                    n = min(child, miniMax(child, depth + 1, maxDepth));
                }
            }
            return n;
        }
    }

    private Node max(Node nodeA, Node nodeB) {
        if (nodeA.getValue() > nodeB.getValue())
            return nodeA;

        return nodeB;
    }

    private Node min(Node nodeA, Node nodeB) {
        if (nodeA.getValue() <= nodeB.getValue())
            return nodeA;

        return nodeB;
    }

}
