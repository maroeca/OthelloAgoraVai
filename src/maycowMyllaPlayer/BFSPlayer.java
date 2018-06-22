package maycowMyllaPlayer;

import game.AbstractPlayer;
import game.BoardSquare;
import game.Move;
import game.OthelloGame;

import java.util.List;

public class BFSPlayer extends AbstractPlayer {
    public BoardSquare play(int[][] tab) {
        OthelloGame game = new OthelloGame();
        List<Move> firstMoves = game.getValidMoves(tab, getMyBoardMark());

        Move bestMove = BreadthFirstSearch(tab, 0, 2);
        return bestMove.getBardPlace();
    }

    private Move BreadthFirstSearch(int[][] tab, int depth, int maxDepth) {
        Graph graph = new Graph();
        graph.setupGraph(tab, getMyBoardMark(),depth, maxDepth);

    }


}
