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
            System.out.println("message: " + message);
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
        JSONArray socketAddresses = new JSONArray();
        JSONArray nodeNames = new JSONArray();
        nodeNames.addAll(table.getTable().keySet());
        for(InetSocketAddress node: table.getTable().values()) {
            socketAddresses.add(node.toString());
        }

        nodeNames.add(myName);
        socketAddresses.add(mySocketAddress.toString());
        JSONObject packet = new JSONObject();
        packet.put("Names", nodeNames);
        packet.put("Addresses", socketAddresses);
        return packet;
    }




}
