package nl.applesaph.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server implements ServerInterface, Runnable {

    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private HashMap<Integer, ClientHandler> clientHandlers = new HashMap<>();
    private HashMap<Integer, String> usernames = new HashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            serverThread = new Thread(this);
            serverThread.start();
            System.out.println("Server started at " + port);
        } catch (IOException e) {
            System.out.println("ERROR: Could not start server at port " + port);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            serverThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void newGame(List<ClientHandler> clientHandlers) {

    }

    @Override
    public boolean inGame(ClientHandler clientHandler) {
        return false;
    }

    @Override
    public void handleTurnMessage(ClientHandler client, String message) {

    }

    @Override
    public void handleSetupTurnMessage(ClientHandler client, String message) {

    }

    @Override
    public int checkUsernameLoggedIn(String username) {
        return usernames.keySet().stream().filter(key -> usernames.get(key).equals(username)).findFirst().orElse(-1);
    }

    @Override
    public List<ClientHandler> getLoggedInClients() {
        return null;
    }

    @Override
    public List<ClientHandler> getWaitingClients() {
        return null;
    }

    @Override
    public void removeClient(int playerNumber) {
        clientHandlers.remove(playerNumber);
    }

    @Override
    public void addClient(int playerNumber, ClientHandler clientHandler) {
        clientHandlers.put(playerNumber, clientHandler);
    }

    @Override
    public void sendToAll(String message) {
        clientHandlers.forEach((key, value) -> value.send(message));
    }

    @Override
    public void sendToAllExcept(String message, int[] playerNumbers) {
        clientHandlers.forEach((key, value) -> {
            for (int playerNumber : playerNumbers) {
                if (key != playerNumber) {
                    value.send(message);
                }
            }
        });
    }

    @Override
    public void sendToClient(String message, int playerNumber) {
        clientHandlers.get(playerNumber).send(message);
    }

    @Override
    public void sendToClients(String message, int[] playerNumbers) {
        clientHandlers.forEach((key, value) -> {
            for (int playerNumber : playerNumbers) {
                if (key == playerNumber) {
                    value.send(message);
                }
            }
        });
    }

    @Override
    public void run() {
        boolean running = true;
        while (running && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this,clientHandlers.size()+1);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}
