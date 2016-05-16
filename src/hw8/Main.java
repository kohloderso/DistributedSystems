package hw8;


import java.time.temporal.ChronoField;

public class Main {
    public static void main(String[] args) {
        int m = 5;
        ChordNode node1 = new ChordNode(1, m);
        ChordNode node3 = new ChordNode(3, m);
        ChordNode node7 = new ChordNode(7, m);
        ChordNode node8 = new ChordNode(8, m);
        ChordNode node12 = new ChordNode(12, m);
        ChordNode node15 = new ChordNode(15, m);
        ChordNode node19 = new ChordNode(19,m);
        ChordNode node25 = new ChordNode(25, m);
        ChordNode node27 = new ChordNode(27, m);

        node1.join(null);
        node3.join(node1);
        node7.join(node1);
        node12.join(node3);
        node15.join(node12);
        node8.join(node1);
        node19.join(node3);
        node25.join(node12);
        node27.join(node7);


        System.out.println("Node 1: ");
        System.out.println(node1.printFingers());
        System.out.println("Node 3: ");
        System.out.println(node3.printFingers());
        System.out.println("Node 7: ");
        System.out.println(node7.printFingers());
        System.out.println("Node 12: ");
        System.out.println(node12.printFingers());
        System.out.println("Node 25: ");
        System.out.println(node25.printFingers());
        System.out.println("Node 27: ");
        System.out.println(node27.printFingers());

        ChordNode test = node1.findSuccessor(7);
        System.out.println(test.toString());

        System.out.println("Sending message from 1 to 1");
        node1.sendMSG(1, "Hello 1");
        System.out.println("Sending message from 25 to 8");
        node25.sendMSG(8, "Hello 8");
        System.out.println(node25.sendMSG(19, ""));

        //createFullRing(12);
    }

    public static void createFullRing(int m) {
        int max = (int) Math.pow(2,m);
        ChordNode[] nodes = new ChordNode[max];

        ChordNode firstNode = new ChordNode(0, m);
        firstNode.join(null);
        nodes[0] = firstNode;
        for(int i = 1; i < max; i++) {
            ChordNode node = new ChordNode(i, m);
            node.join(firstNode);
            nodes[i] = node;
        }

        int maxHops = 0;
        double avgHops = 0;
        for(int i = 0; i < max; i++) {
            for(int j = 0; j < max; j++) {
                int hops = nodes[i].sendMSG(nodes[j].getID(), "");
                if(hops > maxHops) maxHops = hops;
                avgHops = avgHops + (double)hops/(max * max);
            }
        }
        System.out.println("average Hops: " + avgHops);
        System.out.println("maxHops Hops: " + maxHops);
    }
}
