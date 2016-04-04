package hw2.simpleClientServer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;


public class Protocol {
    static final int SERVER_PORT = 4444;

    /**
     * Client side of communication. Send request to server in JSON.
     * @param socket: Socket connected to the server
     */
    static void request(Socket socket, String username, String operation, int... operands) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("operation", operation);
        JSONArray jsonoperands = new JSONArray();
        for(int operand: operands) {
            jsonoperands.add(operand);
        }
        jsonObject.put("operands", jsonoperands);

        // send Information to Server
        sendJSON(jsonObject, socket);
    }

    static boolean authenticate(String username, Socket socket) {
        // send username
        JSONObject userObj = new JSONObject();
        userObj.put("username", username);
        Protocol.sendJSON(userObj, socket);

        // receive answer
        JSONObject answerObj = receiveJSON(socket);
        Boolean answer = (Boolean) answerObj.get("authenticated");
        return answer;
    }

    static int getResult(Socket socket) {
        JSONObject resultObj = receiveJSON(socket);
        Long result = (Long) resultObj.get("result");
        return result.intValue();
    }


    /**
     *
     * @param socket: serverSocket that has client connected to it
     */
    static int processRequest(Socket socket) {
        Long result = new Long(0);

        JSONObject requestObj = receiveJSON(socket);
        String operation = (String) requestObj.get("operation");
        JSONArray operands = (JSONArray) requestObj.get("operands");

        switch (operation) {
            case "+": result = (Long)operands.get(0) + (Long)operands.get(1); break;
            case "-": result = (Long)operands.get(0) + (Long)operands.get(1); break;
            case "*": result = (Long)operands.get(0) + (Long)operands.get(1); break;
            case "lucas": result = lucas(((Long)operands.get(0)).intValue()); break;
            default:
                System.out.println("Operation not supported");
        }

        return result.intValue();
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


    public static long lucas(final int n)
    {
        return lucasTailRec(2, 1, n);
    }

    private static long lucasTailRec(final long a, final long b, final int n)
    {
        return n < 1 ? a : n == 1 ?  b : lucasTailRec(b, a + b, n - 1);
    }


}
