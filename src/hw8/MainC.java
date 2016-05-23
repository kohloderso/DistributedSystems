package hw8;

import java.util.Random;

/**
 * Created by Christina on 16.05.2016.
 */
public class MainC {

    public static void main(String[] args) {

        int n = 100;
        int m = 12;
        int max = (int) Math.pow(2,m);
        Random rnd = new Random();

        ChordNode firstNode = new ChordNode(rnd.nextInt(max), m);
        firstNode.join(null);

        ChordNode[] nodes = new ChordNode[n];
        nodes[0] = firstNode;
        System.out.println("First node is " + firstNode.getID());

        for(int i = 1; i < n; i++) {
            int id = rnd.nextInt(max);
            while(firstNode.findSuccessor(id).getID() == id) { // if the ID is already taken, generate a new one
                id = rnd.nextInt(max);
            }
            System.out.println("Creating new node: " + id);
            ChordNode node = new ChordNode(id, m);
            node.join(firstNode);
            nodes[i] = node;
        }


        int maxHops = 0;
        double avgHops = 0;
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                int hops = nodes[i].sendMSG(nodes[j].getID(), "");
                if(hops > maxHops) maxHops = hops;
                avgHops = avgHops + (double)hops/(n*n);
            }
        }
        System.out.println("average Hops: " + avgHops);
        System.out.println("maxHops Hops: " + maxHops);
    }

}
