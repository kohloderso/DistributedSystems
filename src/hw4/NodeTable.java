package hw4;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by christina on 14.04.16.
 */
public class NodeTable {
    private String myName;
    private HashMap<String, InetSocketAddress> nodes = new HashMap<>();
    private int n;
    private Random rnd = new Random();

    public NodeTable(int n, String myName, String initName, InetSocketAddress initAddress) {
        this.n = n;
        this.myName = myName;
        if(initName != null) {
            nodes.put(initName, initAddress);
        }
    }


    public void delete(String name) {
        nodes.remove(name);
    }

    public void merge(String[] names, InetSocketAddress[] addresses) {
        for(int i = 0; i < names.length; i++) {
            if(names[i] != myName) {
                nodes.put(names[i],addresses[i]);
            }
        }
        chooseSubset();
    }

    public void chooseSubset() {
        while(nodes.size() >= n) {
            int randInt = rnd.nextInt(nodes.size());
            String randName = nodes.keySet().toArray(new String[nodes.size()])[randInt];
            nodes.remove(randName);
        }
    }

    public String getRandomName() {
        if(nodes.isEmpty()) return null;
        int randInt = rnd.nextInt(nodes.size());
        return nodes.keySet().toArray(new String[nodes.size()])[randInt];
    }

    public InetSocketAddress getAddress(String name) {
        return nodes.get(name);
    }

    public HashMap<String, InetSocketAddress> getTable() {
        return nodes;
    }
}
