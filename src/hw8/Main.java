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
    }
}
