package server;

import com.google.gson.*;
import dto.DatabaseRequest;
import dto.DatabaseResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

import static server.Main.lock;

public class Session implements Callable<Boolean> {
    private Socket socket;

    private JsonObject database;

    public Session(Socket socket, JsonObject database) {
        this.socket = socket;
        this.database = database;
    }

    @Override
    public Boolean call() throws Exception {
        try (
                var input = new DataInputStream(socket.getInputStream());
                var output = new DataOutputStream(socket.getOutputStream());
        ) {

            DatabaseRequest databaseRequest = new Gson().fromJson(input.readUTF(), DatabaseRequest.class);
            DatabaseResponse databaseResponse = null;
            switch (databaseRequest.getType()) {
                case "exit":
                    output.writeUTF(new Gson().toJson(new DatabaseResponse("OK")));
                    return false;
                case "get":
                    databaseResponse = get(databaseRequest.getKey());
                    break;
                case "set":
                    databaseResponse = set(databaseRequest.getKey(), databaseRequest.getValue());
                    break;
                case "delete":
                    databaseResponse = delete(databaseRequest.getKey());
                    break;

            }

            String jsonString = new Gson().toJson(databaseResponse);
            output.writeUTF(jsonString);
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public DatabaseResponse get(JsonElement key) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            JsonElement value;
            if (key.isJsonArray()) {
                JsonArray jsonArray = key.getAsJsonArray();
                value = findJsonObject(jsonArray, 0, database);
            } else if (key.isJsonObject()) {
                value = database.get(key.toString()).getAsJsonObject();
            } else {
                throw new RuntimeException();
            }
            DatabaseResponse databaseResponse = new DatabaseResponse("OK");
            databaseResponse.setValue(value);
            return databaseResponse;
        } catch (IndexOutOfBoundsException e) {
            DatabaseResponse response = new DatabaseResponse("ERROR");
            response.setReason("No such key");
            return response;
        } finally {
            readLock.unlock();
        }
    }



    public DatabaseResponse set(JsonElement key, JsonElement value) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if(key.isJsonArray()){
                JsonArray jsonArray = key.getAsJsonArray();
                setJsonObject(jsonArray, 0, database, value);

            } else {
                database.add(key.toString().replace("\"", ""), value);
            }

            return new DatabaseResponse("OK");
        } finally {
            writeLock.unlock();
        }


    }



    public DatabaseResponse delete(JsonElement key) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            JsonElement value;
            if(key.isJsonArray()){
                JsonArray jsonArray = key.getAsJsonArray();
                value = deleteJsonObject(jsonArray, 0, database);
            } else {
                value = database.remove(key.toString());
            }
            if (value == null) {
                DatabaseResponse response = new DatabaseResponse("ERROR");
                response.setReason("No such key");
                return response;
            }
            return new DatabaseResponse("OK");
        } finally {
            writeLock.unlock();
        }
    }

    private JsonElement deleteJsonObject(JsonArray jsonArray, int start, JsonElement current) {

        if(start==jsonArray.size()-1){
            return current.getAsJsonObject().remove(jsonArray.get(start).toString().replace("\"",""));
        }
        return deleteJsonObject(jsonArray, start+1, current.getAsJsonObject().get(jsonArray.get(start).getAsString()));
    }

    private JsonElement findJsonObject(JsonArray jsonArray, int start, JsonElement jsonElement) {
        if(start==jsonArray.size()){
            return jsonElement;
        }
        return findJsonObject(jsonArray, start+1, jsonElement.getAsJsonObject().get(jsonArray.get(start).getAsString()));
    }
    private void setJsonObject(JsonArray jsonArray, int start, JsonElement current, JsonElement value) {
        if(start==jsonArray.size()-1){
            JsonObject jsonObject= current.getAsJsonObject();
            jsonObject.add(jsonArray.get(start).toString().replace("\"", ""), value);
            return ;
        }
         setJsonObject(jsonArray, start+1,  current.getAsJsonObject().get(jsonArray.get(start).getAsString()), value);
    }
}
