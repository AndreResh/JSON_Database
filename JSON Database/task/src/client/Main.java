package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dto.DatabaseRequest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Main {


    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 8080;
        System.out.println("Client started!");
        Socket socket = new Socket(InetAddress.getByName(address), port);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        ClientArgs clientArgs = new ClientArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(clientArgs)
                .build();
        jCommander.parse(args);

        String jsonObject;
        if (clientArgs.getFileName() != null) {
            jsonObject = createRequestFromFile(clientArgs.getFileName());
        } else {
            DatabaseRequest databaseRequest = createRequestFromArgs(clientArgs);
            jsonObject = new Gson().toJson(databaseRequest);
        }

        output.writeUTF(jsonObject);

        System.out.printf("Sent: %s\n", jsonObject);
        System.out.printf("Received: %s\n", input.readUTF());

        socket.close();
    }

    private static String createRequestFromFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        try {

            try(var bufferReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/client/data/" + fileName))) {
                while (bufferReader.ready()){
                    builder.append(bufferReader.readLine());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private static DatabaseRequest createRequestFromArgs(ClientArgs clientArgs) {
        DatabaseRequest databaseRequest = new DatabaseRequest(clientArgs.getType());
        if (clientArgs.getKey() != null) {
            databaseRequest.setKey(new JsonPrimitive(clientArgs.getKey()));
        }
        if (clientArgs.getValue() != null) {
            databaseRequest.setValue(new JsonPrimitive(clientArgs.getValue()));
        }
        return databaseRequest;

    }

}
