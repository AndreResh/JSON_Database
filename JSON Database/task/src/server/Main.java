package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Main {
    public static ReentrantReadWriteLock lock;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        String address = "127.0.0.1";
        int port = 8080;
        System.out.println("Server started!");
        ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));
        JsonObject database = readFromFile();

        lock = new ReentrantReadWriteLock(true);
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            Future<Boolean> future = executorService.submit(new Session(server.accept(), database));
            if(!future.get()){
                break;
            }
        }

        if(database.size()!=0) {
            writeToDB(database);
        }
        server.close();
        executorService.shutdown();
    }

    private static JsonObject readFromFile() {
        try(var bufferReader = new BufferedReader(new FileReader("/home/andrew/IdeaProjects/JSON Database/JSON Database/task/src/server/data/db.json"))) {
            JsonObject object= new Gson().fromJson(bufferReader, JsonObject.class);
            if(object==null){
                return new JsonObject();
            }
            return object;
        } catch (IOException e) {
            return new JsonObject();
        }
    }

    private static void writeToDB(JsonObject object) {
        try(var bufferWriter = new BufferedWriter(new FileWriter("/home/andrew/IdeaProjects/JSON Database/JSON Database/task/src/server/data/db.json"))) {
            bufferWriter.write(object.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
