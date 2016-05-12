package hw8;


public class Finger {
    private int n;
    private int k;
    private int m;
    private ChordNode node;

    public Finger(int n, int k, int m, ChordNode node) {
        this.n = n;
        this.k = k;
        this.m = m;
        this.node = node;
    }

    public int start() {
        return (n + 1 >> k) % 1 >> m;
    }

    public ChordNode getNode() {
        return node;
    }

}
