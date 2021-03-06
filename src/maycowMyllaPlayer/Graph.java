package maycowMyllaPlayer;

import game.BoardSquare;
import game.Move;
import game.OthelloGame;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private Node startNode;
    private ArrayList<Node> nodes;
    private float h1, h2, h3, h4;

    Graph() {
        nodes = new ArrayList<Node>();
        reset();
        h1 = h2 = h3 = h4 = 1f;
    }

    Graph(float heuristica1, float heuristica2, float heuristica3, float heuristica4){
        nodes = new ArrayList<Node>();
        reset();
        h1 = heuristica1;
        h2 = heuristica2;
        h3 = heuristica3;
        h4 = heuristica4;
    }

    public void reset() {
        startNode = null;
        nodes.clear();

    }

    private void setStartNode(int[][] tab, int player) {
        startNode = new Node(-1, tab, null);
        startNode.setPlayer(player);
        nodes.add(startNode);
    }

    public void addNode(Node newNode, Node parent, Move move) {
        //System.out.println("Value on node AddNode: " + newNode.getValue());
        newNode.setMove(move);
        parent.addChild(newNode);
        //parent.setValue(newNode.getValue());
        this.nodes.add(newNode);
        //System.out.println("Value on node AddNode: " + newNode.getValue());
    }

    public Node getStartNode() {
        return this.startNode;
    }

    public void setupGraph(int[][] tab, int player, int maxDepth) {
        setStartNode(tab, player);
        simulateStates(tab, startNode, player, 0, maxDepth);
        //System.out.println("Setup");
    }

    private void simulateStates(int[][] tab, Node parentNode, int player, int depth, int maxDepth) {
        //System.out.println("Depth on start of simulateStates: " + depth + " Player: " + player);
        if (depth > maxDepth) {
            return;
        }

        OthelloGame game = new OthelloGame();
        List<Move> validMoves = game.getValidMoves(tab, player);

        for (Move m : validMoves) {
            int[][] board = simulateMove(tab, m.getBardPlace(), player);  //cria um novo board com o movimento M simulado

            if (!game.noSpace(board)) { //verifica se o jogo nao acabou

                Node newNode = new Node(depth, board, parentNode); //cria um novo node
                newNode.setPlayer(player * (-1));
                simulateStates(board, newNode, player * (-1), depth + 1, maxDepth);  //recursividade. Vai repetir ate que depth seja >= maxDepth

                if(depth == maxDepth) {
                    newNode.setValue(calculateHeuristics(m, board, player, game));
                    //newNode.setParentValue(newNode.getValue());
                    //System.out.println(newNode.getValue());
                }

                //System.out.println("Value on node addNode: " + newNode.getValue());

                this.addNode(newNode, parentNode, m); //adiciona o node no array
               // System.out.println("Value on node addNode: " + newNode.getValue());


            } else {
                if (isMyPlayerWinner(player, board)) {
                    Node newNode =  new Node(depth, board, parentNode);
                    newNode.setValue(Double.POSITIVE_INFINITY);
                    newNode.setPlayer(player);
                    this.addNode(newNode, parentNode, m);

                } else {
                    Node newNode = new Node(depth, board, parentNode);
                    newNode.setValue(Double.NEGATIVE_INFINITY);
                    newNode.setPlayer(player);
                    this.addNode(newNode, parentNode, m);
                }
            }
        }

    }

    /**
     * Funcoes relacionadas ao grafo ou criaçao dele
     */

    public ArrayList<Node> getNodesFromDepth(int depth) {
        ArrayList<Node> aux = new ArrayList<Node>();
        for(Node n : this.nodes) {
            if (n.getDepth() == depth)
                aux.add(n);
        }

        return aux;
    }

    public boolean isMyPlayerWinner(int player, int[][] tab) {
        int myPlayer = 0;
        int opPlayer = 0;

        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                if (tab[i][j] == player)
                    myPlayer++;
                else if (tab[i][j] == (player * -1))
                    opPlayer++;
                else
                    System.err.println("UER deu ruim na contagem no isMyPlayerWinner");
            }
        }

        if (myPlayer > opPlayer)
            return true;

        return false;
    }


    /**
     * Heuristicas e funçoes auxiliares relacionadas.
     * Descriçoes das heuristicas no GreedyPlayer
     */

    private double calculateHeuristics(Move m, int[][] tab, int player, OthelloGame game) {
        double heuristic = 0.0;

        heuristic += parityHeuristic(m, tab, player, game) * h1;
        heuristic += mobilityHeuristic(m, tab, player, game) * h2;
        heuristic += cornerHeuristic(m, tab, player, game) * h3;
        heuristic += stabilityHeuristic(m, tab, player, game) * h4;

        return heuristic;
    }

    private double parityHeuristic(Move move, int [][] tab, int player, OthelloGame game) {
        //System.out.println(tab);
        int [][] board = simulateMove(tab, move.getBardPlace(), player);
        //System.out.println(board);

        double myPlayer = countPlayerMarks(board, player);
        double opPlayer = countPlayerMarks(board, (player * -1));

        //System.out.println("MY PLAYER: " + myPlayer + "- OP PLAYER: " + opPlayer);
        //System.out.println("PARIDADE " + 100 * ((myPlayer - opPlayer) / (myPlayer + opPlayer)));
        if (myPlayer > opPlayer)
            return (double) 100 * ((myPlayer - opPlayer) / (myPlayer + opPlayer));
        if (myPlayer < opPlayer)
            return (double) -(100 * ((opPlayer - myPlayer) / (myPlayer + opPlayer)));
        return 0.0;
    }

    private double mobilityHeuristic(Move move, int [][] tab, int player, OthelloGame game) {
        int [][] board = simulateMove(tab, move.getBardPlace(), player);

        int myMoves = game.getValidMoves(board, player).size();
        int enemyMoves = game.getValidMoves(board, (player * -1)).size();

        if (myMoves + enemyMoves != 0) {
            if (myMoves > enemyMoves)
                return (double) 100 * (myMoves - enemyMoves) / (myMoves + enemyMoves);
            else if (myMoves < enemyMoves) {
                return (double) -(100 * (enemyMoves - myMoves) / (myMoves + enemyMoves));
            }
        }
        return 0.0;
    }

    private double cornerHeuristic(Move move, int[][] tab, int player,  OthelloGame game) {
        int [][] board = simulateMove(tab, move.getBardPlace(), player);
        int myCorners = countCorners(board, player);
        int enemyCorners = countCorners(board, (player * -1));

        if (myCorners + enemyCorners != 0) {
            if (myCorners > enemyCorners)
                return (double) 100 * (myCorners - enemyCorners) / (myCorners + enemyCorners);
            else if (myCorners < enemyCorners) {
                return (double) -(100 * (enemyCorners - myCorners) / (myCorners + enemyCorners));
            }
        }
        return 0.0;
    }

    private double stabilityHeuristic(Move move, int[][] tab, int player, OthelloGame game) {
        int [][] board = simulateMove(tab, move.getBardPlace(), player);
        double myValue = 0.0;
        double enemyValue = 0.0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {

                if(board[i][j] == player) {
                    myValue += calculateStability(board, i, j, player);

                } else if (board[i][j] == (player * -1)) {
                    enemyValue += calculateStability(board, i, j, (player * -1));
                }

            }
        }

        if(myValue + enemyValue != 0)
            return (double) 100 * (myValue - enemyValue) / (myValue + enemyValue);
        else
            return 0;
    }

    private double calculateStability(int[][] tab, int x, int y, int player) {
        int playerCount = 0;
        int otherCount = 0;
        int emptyCount = 0;

        if (isAtCorner(x, y, tab.length, tab[x].length)) //ta no canto
            return 1.0;

        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (i < 0 || i >= tab.length || j < 0 || j >= tab[x].length) //out of bounds
                    continue;

                if (i == x && j == y)
                    continue;

                else {
                    if (tab[i][j] == 0)
                        emptyCount++;

                    else if(tab[i][j] == player)
                        playerCount++;

                    else
                        otherCount++;

                }
            }
        }

        if (playerCount >= otherCount && playerCount >= emptyCount)
            return 0.5;
        else if(emptyCount >= playerCount && emptyCount >= otherCount)
            return -1;
        else
            return -0.5;
    }

    private boolean isAtCorner(int x, int y, int width, int height) {
        if (x == 0 || x == width) {
            if (y == 0 || y == height)
                return true;
        }
        return false;
    }

    private int countCorners(int [][] tab, int player) {
        int countPlayer = 0;
        try {
            for (int i = 0; i < tab.length; i += (tab.length - 1)) {
                for(int j = 0; j < tab[i].length; j += (tab[i].length - 1)) {
                    if (tab[i][j] == player)
                        countPlayer++;
                }
            }
        } catch (Exception e) {
            if (player != 1 && player != -1)
                System.err.println(e + " Invalid player");
        }

        return countPlayer;
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
        return simulatedBoard;
    }

}
