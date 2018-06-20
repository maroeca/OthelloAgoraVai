package maycowMyllaPlayer;

import game.*;

import java.util.List;

public class GreedyPlayer extends AbstractPlayer {

    @Override
    public BoardSquare play(int[][] tab) {
        OthelloGame game = new OthelloGame();
        List<Move> moves = game.getValidMoves(tab, getMyBoardMark());

        Move bestGreedyMove = greedySearch(moves, tab, game);
        return bestGreedyMove.getBardPlace();
    }

    /**
     * Todas as heuristicas serão calculadas aqui
     *   HEUTISTICAS:
     *   Fonte: https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-reversiothello/
     *   1. Paridade da quantidade de peças dos players.
     *   2. Diferença relativa dos movimentos possiveis de cada player. Assim o player tenta reduzir a mobilidade do inimigo
     **/


    private Move greedySearch(List<Move> possibleMoves, int [][] tab, OthelloGame game) {
        double heuristic = 0.0;
        double bestHeuristic = Double.NEGATIVE_INFINITY;
        Move bestMove = possibleMoves.get(0);

        for (Move m: possibleMoves) {

            heuristic = parityHeuristic(m, tab, game) + mobilityHeuristic(m, tab, game); // + outras heuristica
            System.out.println("PARITY: " + parityHeuristic(m, tab, game) + " || MOBILITY: " + mobilityHeuristic(m, tab, game) + " || total: " + heuristic);
            if (bestHeuristic < heuristic) {
                bestHeuristic = heuristic;
                bestMove = m;

            }
        }

        return bestMove;
    }

    private double parityHeuristic(Move move, int [][] tab, OthelloGame game) {
        //System.out.println(tab);
        int [][] board = simulateMove(tab, move.getBardPlace(), getMyBoardMark());
        //System.out.println(board);

        double myPlayer = countPlayerMarks(board, getMyBoardMark());
        double opPlayer = countPlayerMarks(board, getOpponentBoardMark());

        //System.out.println("MY PLAYER: " + myPlayer + "- OP PLAYER: " + opPlayer);
        //System.out.println("PARIDADE " + 100 * ((myPlayer - opPlayer) / (myPlayer + opPlayer)));
        return (double) 100 * ((myPlayer - opPlayer) / (myPlayer + opPlayer));
    }

    private double mobilityHeuristic(Move move, int [][] tab, OthelloGame game) {
        int [][] board = simulateMove(tab, move.getBardPlace(), getMyBoardMark());

        int myMoves = game.getValidMoves(board, getMyBoardMark()).size();
        int enemyMoves = game.getValidMoves(board, getOpponentBoardMark()).size();

        if(myMoves + enemyMoves != 0) {
            return (double) 100 * (myMoves - enemyMoves) / (myMoves * enemyMoves);
        } else {
            return 0.0;
        }
    }

    private double countPlayerMarks(int [][] tab, int player) {
        int countPlayer = 0;
        try {
            for (int i = 0; i < tab.length; i++) {
                for (int j = 0; j < tab[i].length; j++) {
                    if (tab[i][j] == player)
                        countPlayer++;
                }
            }

        } catch (Exception e) {
            if (player != 1 && player != -1)
                System.err.println(e + " Invalid player");

        }
        return (double) countPlayer;
    }

    //igual a funçao do_move. Mas por algum motivo a do_move tava alterando direto a matriz game, deixando ela irreversivel
    private int[][] simulateMove(int[][] tab, BoardSquare boardPlace, int player) {
        int[][] simulatedBoard = new int[tab.length][tab[0].length];

        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                simulatedBoard[i][j] = tab[i][j];
            }
        }

        //  skip play
        if (boardPlace.getRow() == -1 && boardPlace.getCol() == -1) {
            return simulatedBoard;
        }

        simulatedBoard[boardPlace.getRow()][boardPlace.getCol()] = player;
        // search own mark in backwards line
        int position = -1;
        for (int i = boardPlace.getRow() - 1; i >= 0; i--) {
            // rdr - whether find an empty spot before the player mark
            if (simulatedBoard[i][boardPlace.getCol()] == 0) {
                break;
            }
            if (simulatedBoard[i][boardPlace.getCol()] == player) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            position++;
            for (int i = position; i < boardPlace.getRow(); i++) {
                if (simulatedBoard[i][boardPlace.getCol()] != 0) {
                    simulatedBoard[i][boardPlace.getCol()] = player;
                }
            }
        }
        // search own mark in forward lines
        position = -1;
        for (int i = boardPlace.getRow() + 1; i < 8; i++) {
            // rdr - whether find an empty spot before the player marks
            if (simulatedBoard[i][boardPlace.getCol()] == 0) {
                break;
            }
            if (simulatedBoard[i][boardPlace.getCol()] == player) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            position--;
            for (int i = position; i > boardPlace.getRow(); i--) {
                if (simulatedBoard[i][boardPlace.getCol()] != 0) {
                    simulatedBoard[i][boardPlace.getCol()] = player;
                }
            }
        }
        /**
         * **************************************************************************************
         */
        //search own mark in col in backwards line
        position = -1;
        for (int i = boardPlace.getCol() - 1; i >= 0; i--) {
            //rdr - whether find an empty spot before the player marks
            if (simulatedBoard[boardPlace.getRow()][i] == 0) {
                break;
            }
            if (simulatedBoard[boardPlace.getRow()][i] == player) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            position++;
            for (int i = position; i < boardPlace.getCol(); i++) {
                if (simulatedBoard[boardPlace.getRow()][i] != 0) {
                    simulatedBoard[boardPlace.getRow()][i] = player;
                }
            }
        }
        // search own mark in rows in forward lines
        position = -1;
        for (int i = boardPlace.getCol() + 1; i < 8; i++) {
            // rdr - whether find an empty spot before the player marks
            if (simulatedBoard[boardPlace.getRow()][i] == 0) {
                break;
            }
            if (simulatedBoard[boardPlace.getRow()][i] == player) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            position--;
            for (int i = position; i > boardPlace.getCol(); i--) {
                if (simulatedBoard[boardPlace.getRow()][i] != 0) {
                    simulatedBoard[boardPlace.getRow()][i] = player;
                }
            }
        }
        /**
         * ******************************************************
         */
        // search own mark in backwards diagonals
        int row = boardPlace.getRow() - 1;
        int col = boardPlace.getCol() - 1;
        // rdr - indicates whether an empty spot was found before player mark
        boolean empty = false;

        while (row >= 0 && col >= 0) {
            if (simulatedBoard[row][col] == player) {
                break;
            }
            // rdr - exits loop if an empty spot was found
            if (simulatedBoard[row][col] == 0) {
                empty = true;
                break;
            }
            row--;
            col--;
        }
        if (row >= 0 && col >= 0 && !empty) { // rdr - check if it is empty
            while (row != boardPlace.getRow()
                    && col != boardPlace.getCol()) {
                if (simulatedBoard[row][col] != 0) {
                    simulatedBoard[row][col] = player;
                }
                row++;
                col++;
            }
        }
        //search own mark in forward diagonal
        row = boardPlace.getRow() + 1;
        col = boardPlace.getCol() + 1;

        empty = false;
        while (row < 8 && col < 8) {
            if (simulatedBoard[row][col] == player) {
                break;
            }
            if (simulatedBoard[row][col] == 0) {
                empty = true;
                break;
            }
            row++;
            col++;
        }
        if (row < 8 && col < 8 && !empty) {
            while (row != boardPlace.getRow()
                    && col != boardPlace.getCol()) {
                if (simulatedBoard[row][col] != 0) {
                    simulatedBoard[row][col] = player;
                }
                row--;
                col--;
            }
        }
        /**
         * ******************************************************
         */
        //search own mark in backwards inverse diagonal
        row = boardPlace.getRow() - 1;
        col = boardPlace.getCol() + 1;
        empty = false;
        while (row >= 0 && col < 8) {
            if (simulatedBoard[row][col] == player) {
                break;
            }
            if (simulatedBoard[row][col] == 0) {
                empty = true;
                break;
            }
            row--;
            col++;
        }
        if (row >= 0 && col < 8 && !empty) {
            while (row != boardPlace.getRow()
                    && col != boardPlace.getCol()) {
                if (simulatedBoard[row][col] != 0) {
                    simulatedBoard[row][col] = player;
                }
                row++;
                col--;
            }
        }
        //search own mark in forwards inverse diagonal
        row = boardPlace.getRow() + 1;
        col = boardPlace.getCol() - 1;
        empty = false;
        while (row < 8 && col >= 0) {
            if (simulatedBoard[row][col] == player) {
                break;
            }
            if (simulatedBoard[row][col] == 0) {
                empty = true;
                break;
            }
            row++;
            col--;
        }
        if (row < 8 && col >= 0 && !empty) {
            while (row != boardPlace.getRow()
                    && col != boardPlace.getCol()) {
                if (simulatedBoard[row][col] != 0) {
                    simulatedBoard[row][col] = player;
                }
                row--;
                col++;
            }
        }
        /**
         * ******************************************************
         */
        System.out.println(simulatedBoard);
        return simulatedBoard;
    }
}

