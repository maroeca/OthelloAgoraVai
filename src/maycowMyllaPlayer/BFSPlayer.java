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


        Move bestMove = breadthFirstSearch(tab, 2);

        return bestMove.getBardPlace();
    }

    private Move breadthFirstSearch(int[][] tab, int maxDepth) {
        Graph graph = new Graph();
        graph.setupGraph(tab, getMyBoardMark(), maxDepth);

        Node node = miniMax(graph.getStartNode(), 0, maxDepth);
        System.out.println("Node escolhido" + getFirstMoveOfNode(node).getValue());
        return getFirstMoveOfNode(node).getMove();
    }

    private Node miniMax(Node node, int depth, int maxDepth) {
        if (depth > maxDepth || !node.hasChildren()) {
            return node;
        }
        else {
            if (node.getPlayer() == getMyBoardMark()) { //MAX
                Node bestNode = node;
                for(Node child : node.getChildren()) {

                    Node n = miniMax(child, depth + 1, maxDepth);
                    bestNode = max(bestNode, n);

                }
                return bestNode;

            } else { //MIN
                Node bestNode = node;
                for(Node child : node.getChildren()) {
                    //System.out.println(child.getValue());
                    Node n = miniMax(child, depth + 1, maxDepth);
                    bestNode = min(bestNode, n);

                }
                return bestNode;
            }
            //System.out.println("n: " + n.getChildren().get(0).getValue());
        }
    }

    private Node getFirstMoveOfNode(Node node) {
        if (node.getDepth() <= 0)
            return node;

        return getFirstMoveOfNode(node.getParent());
    }

    private Node max(Node nodeA, Node nodeB) {
        if (nodeA.getValue() > nodeB.getValue())
            return nodeA;

        return nodeB;
    }

    private Node min(Node nodeA, Node nodeB) {
        if (nodeA.getValue() < nodeB.getValue())
            return nodeA;

        return nodeB;
    }

}
