package hw8;


public class ChordNode {
    private int n;
    private int m;
    private int max;
    private ChordNode[] fingerTable;
    private ChordNode predecessor;


    public ChordNode(int id, int m) {
        this.n = id;
        this.m = m;
        fingerTable = new ChordNode[m];
        max = (int) Math.pow(2,m);
    }

    public ChordNode successor() {
        return fingerTable[0];
    }

    public ChordNode findSuccessor(int id) {
        if(inInterval(n, fingerTable[0].n, id)) return fingerTable[0];

        // find largest entry in the fingertable that is still smaller than the n we're looking for
        ChordNode node = fingerTable[0];
        int i = 0;
        if(node.n > id) {
            while(i < m-1 && fingerTable[i].n > id) {
                node = fingerTable[i];
                i++;
            }
        }
        while(i < m-1 && fingerTable[i].n <= id) {
            node = fingerTable[i];
            i++;
        }
        return node.findSuccessor(id);
    }

    public ChordNode findPredecessor(int id) {
        return findSuccessor(id).predecessor;
    }


    public void join(ChordNode node) {
        if(node != null) {
            init_finger_table(node);
            predecessor.newNode(this);
        } else {    // the current node is the only one in the network
            for(int i = 0; i < m; i++) {
                fingerTable[i] = this;
            }
            predecessor = this;
        }
    }

    private void init_finger_table(ChordNode node) {
        // initialize the first entry in the fingertable which is the successor of this node
        fingerTable[0] = node.findSuccessor(n);
        // set the predecessor and the predecessor of the successor
        predecessor = successor().predecessor;
        successor().predecessor = this;


        for(int k = 1; k < m; k++) {
            int fingerStart = (n + (int)Math.pow(2, k)) % (int) Math.pow(2,m);
            fingerTable[k] = node.findSuccessor(fingerStart);
        }
    }

    public void newNode(ChordNode node) {
        if(this == node) return;
        // check if the table needs an update with the new node
        for(int k = 0; k < m; k++) {
            int fingerStart = (n + (int)Math.pow(2, k)) % max;
            if(inInterval(fingerStart, fingerTable[k].n, node.n)) {
                fingerTable[k] = node;
            }
        }
        predecessor.newNode(node);
    }
    private void update_others() {
        for(int i = 0; i < m; i++) {
            ChordNode p = findPredecessor(n - (int)Math.pow(2, i));
            while(p.getFinger(i).n > n) {
                p.setFinger(i, this);
                p = p.predecessor;
            }
        }
    }

    public void sendMSG(int nodeID) {

    }

    public void setFinger(int i, ChordNode node) {
        fingerTable[i] = node;
    }

    public ChordNode getFinger(int i) {
        return fingerTable[i];
    }


    public void printFingers() {
        for(int i = 0; i < m; i++) {
            System.out.println(i + ": " + fingerTable[i].n);
        }
    }

    private boolean inInterval(int begin, int end, int id) {
        if(end <= begin) {
            if(id < begin) {
                id += max;
            }
            end += max;
        }
        if(begin <= id && id < end) {
            return true;
        } else {
            return false;
        }
    }

}
