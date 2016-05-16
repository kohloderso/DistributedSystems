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

    /**
     * find the successor of the given id.
     * If there exists a node with the given id returns that node.
     * @param id
     * @return ChordNode with the given id or the next higher one
     */
    public ChordNode findSuccessor(int id) {
        if(id == n) return this;
        if(inInterval(n, fingerTable[0].n, id)) return fingerTable[0];

        // find largest entry in the fingertable that is still smaller than the n we're looking for
        // => id has to be in interval between current node and the next node
        ChordNode node = fingerTable[0];
        ChordNode nextNode = fingerTable[1];
        int i = 0;
        while(!inInterval(node.n, nextNode.n, id)) {
            i++;
            node = nextNode;
            nextNode = fingerTable[(i+1) % m];
            if(i >= m ) return fingerTable[m-1];
        }
        return node.findSuccessor(id);
    }

    /**
     * Return the next node before this id.
     * @param id
     * @return next ChordNode with it's id lower than the given id
     */
    public ChordNode findPredecessor(int id) {
        return findSuccessor(id).predecessor;
    }


    /**
     * Let this node join the network over the given node.
     * The new node has to ask the given node for the successor of new node's id
     * to initialize it's fingertable. It then announces it's presence counterclockwise
     * around the circle via the predecessors.
     * @param node to join over, if it's null, this node is the only member of the network
     */
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

    /**
     * compute the indices for the fingertable and fill it with the corresponding successors.
     * @param node Node to use for finding successors
     */
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

    /**
     * announce the presence of a new node in the network,
     * by recursively going counterclockwise around the ring.
     * @param node the new node
     */
    public void newNode(ChordNode node) {
        if(this == node) return;    // we have reached the beginning again, one round around the network is complete
        // check if the table needs an update with the new node
        for(int k = 0; k < m; k++) {
            int fingerStart = (n + (int)Math.pow(2, k)) % max;
            if(fingerStart == fingerTable[k].n) continue;
            if(inInterval(fingerStart, fingerTable[k].n, node.n)) {
                fingerTable[k] = node;
            }
        }
        predecessor.newNode(node);
    }


    /*private void update_others() {
        for(int i = 0; i < m; i++) {
            ChordNode p = findPredecessor(n - (int)Math.pow(2, i));
            while(p.getFinger(i).n > n) {
                p.setFinger(i, this);
                p = p.predecessor;
            }
        }
    }*/

    /**
     * Helper function to send a message recursively to a given node and count the number of hops along the way.
     * @param nodeID destination of the message
     * @param message
     * @param hops length of path so far
     * @return length of path
     */
    private int sendMsg(int nodeID, String message, int hops) {
        if(nodeID == n) {
            this.sendMsg(message);
            return hops;
        }
        if(inInterval(n, fingerTable[0].n, nodeID)) {
            fingerTable[0].sendMsg(message);
            return hops+1;
        }

        // find largest entry in the fingertable that is still smaller than the n we're looking for
        // => id has to be in interval between current node and the next node
        ChordNode node = fingerTable[0];
        ChordNode nextNode = fingerTable[1];
        int i = 0;
        while(!inInterval(node.n, nextNode.n, nodeID) && i < m) {
            i++;
            node = nextNode;
            nextNode = fingerTable[(i+1) % m];
            //if(i >= m ) fingerTable[m-1].sendMSG(nodeID, message);
        }
        System.out.println("going to node " + node.n);
        return node.sendMsg(nodeID, message, hops+1);
    }

    /**
     * Send a message recursively to a node and count the number of hops.
     * @param nodeID
     * @param message
     * @return the number of hops
     */
    public int sendMSG(int nodeID, String message) {
        return sendMsg(nodeID, message, 0);
    }

    /**
     * Send a message directly to this node. This only prints the message.
     * @param message
     */
    public void sendMsg(String message) {
        System.out.println("Node " + n + " received message: " + message);
    }

    public void setFinger(int i, ChordNode node) {
        fingerTable[i] = node;
    }

    public ChordNode getFinger(int i) {
        return fingerTable[i];
    }


    /**
     * Check if the given id is inside the interval [begin, end[
     * @param begin
     * @param end
     * @param id
     * @return true if id was in [begin, end[
     */
    private boolean inInterval(int begin, int end, int id) {
        if(end < begin) {
            // split interval in two
            // interval 1: [begin,max]
            if(begin <= id && id <= max) return true;
            // interval 2: [0, end[
            else if(0 <= id && id < end) return true;
        }
        else {
            return (begin <= id && id < end);
        }
        return false;
    }

    public String toString() {
        return "Node " + n + "\n" + printFingers();
    }

    public String printFingers() {
        String str = "";
        for(int i = 0; i < m; i++) {
            str += i + ": " + fingerTable[i].n + "\n";
        }
        return str;
    }

    public int getID() {
        return n;
    }
}
