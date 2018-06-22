package maycowMyllaPlayer;

import game.Move;

import java.util.ArrayList;

public class Node {
    private int depth;
    private int [][] tab;
    private Move move;
    private Node parent;
    private int player;  //pro minimax
    private ArrayList<Node> children;
    private double value;

    Node(int depth, int[][] tab, Node parent) {
        setDepth(depth);
        setParent(parent);
        setTab(tab);

        children = new ArrayList<Node>();
    }

    private void setDepth(int d) {
        this.depth = d;
    }

    private void setParent(Node p) {
        this.parent = p;
    }

    private void setTab(int[][] t) {
        int size = t.length;
        this.tab = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.tab[i][j] = t[i][j];
            }
        }
    }

    public void setValue(double v) {
        this.value = v;
    }

    private void setPlayer(int p) {
        this.player = p;
    }

    public void addChild(Node c) {
        this.children.add(c);
    }

    public void setMove(Move m) {
        this.move = m;
    }

    public Move getMove() {
        return this.move;
    }

    public Node getParent (){
        return this.parent;
    }

    public int getDepth() {
        return this.depth;
    }

    public int[][] getTab() {
        return this.tab;
    }

    public double getValue() {
        return value;
    }
}
