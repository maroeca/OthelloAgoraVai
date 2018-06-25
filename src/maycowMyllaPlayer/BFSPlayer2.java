package maycowMyllaPlayer;

import game.AbstractPlayer;
import game.BoardSquare;
import game.Move;
import game.OthelloGame;

import java.util.ArrayList;
import java.util.List;

public class BFSPlayer2 extends AbstractPlayer {
    public BoardSquare play(int[][] tab) {
        OthelloGame game = new OthelloGame();


        Move bestMove = breadthFirstSearch(tab, 4);

        return bestMove.getBardPlace();
    }

    private Move breadthFirstSearch(int[][] tab, int maxDepth) {
        Graph graph = new Graph(1.5f, 1.5f, 1.2f, 1f);
        graph.setupGraph(tab, getMyBoardMark(), maxDepth);

        Node node = miniMax(graph.getStartNode(), 0, maxDepth);
        //System.out.println("BESTNODE: " + node.getValue());
        return getFirstMoveOfNode(node).getMove();
    }

    private Node miniMax(Node node, int depth, int maxDepth) {
        if (depth > maxDepth || !node.hasChildren()) {
            return node;
        }

        else {
            if (node.getPlayer() == getMyBoardMark()) {
                Node bestNode = getTerminalNode(node);

                for(Node child : node.getChildren()) {
                    Node n = miniMax(child, depth + 1, maxDepth);
                    bestNode = min(bestNode, n);
                }
                return bestNode;

            } else {
                Node bestNode = getTerminalNode(node);
                for(Node child : node.getChildren()) {
                    Node n = miniMax(child, depth + 1, maxDepth);
                    bestNode = max(bestNode, n);

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

    private Node getTerminalNode(Node node) {
        if (!node.hasChildren())
            return node;

        return getTerminalNode(node.getChildren().get(0));
    }

    private Node max(Node nodeA, Node nodeB) {
        if (nodeA.getValue() > nodeB.getValue()) {
            //System.out.println("MAX: " + nodeA.getValue());
            return nodeA;
        }
        //System.out.println("MAX: " + nodeB.getValue());
        return nodeB;
    }

    private Node min(Node nodeA, Node nodeB) {
        if (nodeA.getValue() < nodeB.getValue()) {
            //System.out.println("MIN: " + nodeA.getValue());
            return nodeA;
        }

        //System.out.println("MIN: " + nodeB.getValue());
        return nodeB;
    }

}
