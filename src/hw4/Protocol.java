package hw4;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;


public class Protocol {

    static int initPort = 1234;


    static void sendBroadcastMessage(Socket socket, String message, String senderName, int messageID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Broadcast", message);
        jsonObject.put("Sender", senderName);
        jsonObject.put("ID", messageID);
        sendJSON(jsonObject, socket);
    }


    static JSONObject receiveJSON(Socket socket) {
        JSONObject obj = null;
        try {
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            String message = reader.readLine();
            JSONParser parser = new JSONParser();
            obj = (JSONObject)parser.parse(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    static void sendJSON(JSONObject jsonObject, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.write(jsonObject.toJSONString() + "\n");//jsonObject.writeJSONString(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * makes a JSON Object out of the nodetable plus my own socketAddress
     * @param table
     * @param mySocketAddress
     */
    static JSONObject makeJSONObject(NodeTable table, InetSocketAddress mySocketAddress, String myName) {
        JSONArray hostNames = new JSONArray();
        JSONArray ports = new JSONArray();
        JSONArray nodeNames = new JSONArray();
        nodeNames.addAll(table.getNames());
        for(InetSocketAddress address: table.getAddresses()) {
            hostNames.add(address.getHostName());
            ports.add(address.getPort());
        }

        nodeNames.add(myName);
        hostNames.add(mySocketAddress.getHostName());
        ports.add(mySocketAddress.getPort());
        JSONObject packet = new JSONObject();
        packet.put("Addresses", true);
        packet.put("Names", nodeNames);
        packet.put("Hosts", hostNames);
        packet.put("Ports", ports);
        return packet;
    }


    static HashMap<String, InetSocketAddress> parseAddresses(JSONObject message) {
        HashMap<String, InetSocketAddress> map = new HashMap<>();
        JSONArray jsonnames = (JSONArray) message.get("Names");
        JSONArray jsonHosts = (JSONArray) message.get("Hosts");
        JSONArray jsonPorts = (JSONArray) message.get("Ports");
        for(int i = 0; i < jsonnames.size(); i++) {
            InetSocketAddress address = new InetSocketAddress((String) jsonHosts.get(i), ((Long)jsonPorts.get(i)).intValue());
            map.put((String) jsonnames.get(i), address);
        }
        return map;
    }

    static void sendLookupResponse(String nodeName, InetSocketAddress nodeAddress, InetSocketAddress inquirerAddress) {

    }


}
