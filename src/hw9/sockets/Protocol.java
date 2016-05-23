package hw9.sockets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class Protocol {
    public static final int SERVER_PORT = 4444;

    /**
     * Client side of communication. Send request to server in JSON.
     * @param socket: Socket connected to the server
     */
    static void request(Socket socket, String operation, int... operands) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operation", operation);
        JSONArray jsonoperands = new JSONArray();
        for(int operand: operands) {
            jsonoperands.add(operand);
        }
        jsonObject.put("operands", jsonoperands);
        try {
            // send Information to Server
            OutputStream out = socket.getOutputStream();
            PrintWriter outToServer = new PrintWriter(out);
            jsonObject.writeJSONString(outToServer);
            outToServer.flush();
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int getResult(Socket socket) {
        Long result = new Long(0);
        try {
            InputStreamReader inFromServer = new InputStreamReader(socket.getInputStream());
            JSONParser parser = new JSONParser();
            JSONObject resultObj = (JSONObject)parser.parse(inFromServer);
            result = (Long) resultObj.get("result");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.intValue();
    }

    /**
     *
     * @param socket: serverSocket that has client connected to it
     */
    static int processRequest(Socket socket) {
        Long result = new Long(0);

        try {
            InputStreamReader inFromClient = new InputStreamReader(socket.getInputStream());
            JSONParser parser = new JSONParser();
            JSONObject requestObj = (JSONObject)parser.parse(inFromClient);
            String operation = (String) requestObj.get("operation");
            JSONArray operands = (JSONArray) requestObj.get("operands");

            switch (operation) {
                case "+": result = (Long)operands.get(0) + (Long)operands.get(1); break;
                default:
                    System.out.println("Operation not supported");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result.intValue();
    }

    static void sendResult(Socket socket, int result) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", result);

        // send Information to Client
        try {
            PrintWriter outToClient = new PrintWriter(socket.getOutputStream());
            resultObj.writeJSONString(outToClient);
            outToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
