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

    static void sendShutdownRequest(Socket socket) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("shutdown", true);
        sendJSON(jsonObject, socket);
    }

    static void sendSimpleMessage(Socket socket, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        sendJSON(jsonObject, socket);
    }


    static void sendResult(Socket socket, int result) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", result);

        sendJSON(resultObj, socket);
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
        nodeNames.addAll(table.getTable().keySet());
        for(InetSocketAddress node: table.getTable().values()) {
            hostNames.add(node.getHostName());
            ports.add(node.getPort());
        }

        nodeNames.add(myName);
        hostNames.add(mySocketAddress.getHostName());
        ports.add(mySocketAddress.getPort());
        JSONObject packet = new JSONObject();
        packet.put("Names", nodeNames);
        packet.put("Hosts", hostNames);
        packet.put("Ports", ports);
        return packet;
    }


    static HashMap<String, InetSocketAddress> receiveAndParse(Socket socket) {
        HashMap<String, InetSocketAddress> map = new HashMap<>();
        JSONObject message = receiveJSON(socket);
        JSONArray jsonnames = (JSONArray) message.get("Names");
        JSONArray jsonHosts = (JSONArray) message.get("Hosts");
        JSONArray jsonPorts = (JSONArray) message.get("Ports");
        for(int i = 0; i < jsonnames.size(); i++) {
            InetSocketAddress address = new InetSocketAddress((String) jsonHosts.get(i), ((Long)jsonPorts.get(i)).intValue());
            map.put((String) jsonnames.get(i), address);
        }
        return map;
    }



}
