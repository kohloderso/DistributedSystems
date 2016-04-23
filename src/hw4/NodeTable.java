package hw4;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class NodeTable {
    private String myName;
    private ConcurrentHashMap<String, InetSocketAddress> nodes = new ConcurrentHashMap<>();
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


    public void merge(HashMap<String, InetSocketAddress> newTable) {
        Set<Map.Entry<String, InetSocketAddress>> copy = getEntries();
        nodes.putAll(newTable);
        nodes.remove(myName);
        chooseSubset();
        //if(getEntries().equals(copy))
          //  System.out.println("new table " + myName + ": " + nodes.keySet());
    }

    public void chooseSubset() {
        while(nodes.size() > n) {
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

    public int getSize() {
        return nodes.size();
    }

    public Set<Map.Entry<String, InetSocketAddress>> getEntries() {
        return nodes.entrySet();
    }

    public InetSocketAddress getAddress(String name) {
        return nodes.get(name);
    }

    public  Collection<String> getNames() {
        return nodes.keySet();
    }

    public Collection<InetSocketAddress> getAddresses() {
        return nodes.values();
    }



}
