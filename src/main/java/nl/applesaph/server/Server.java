package nl.applesaph.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server implements ServerInterface, Runnable {

    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private List<ClientHandler> clientHandlers;

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
    public boolean checkUsernameLoggedIn(String username) {
        return false;
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
    public void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    @Override
    public void addClient(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    @Override
    public void sendToAll(String message) {
        clientHandlers.forEach(clientHandler -> clientHandler.send(message));
    }

    @Override
    public void sendToAllExcept(String message, List<ClientHandler> clientHandlerList) {
        clientHandlers.stream().filter(clientHandler -> !clientHandlerList.contains(clientHandler)).forEach(clientHandler -> clientHandler.send(message));
    }

    @Override
    public void sendToClient(String message, ClientHandler clientHandler) {
        clientHandler.send(message);
    }

    @Override
    public void sendToClients(String message, List<ClientHandler> clientHandlers) {
        clientHandlers.forEach(clientHandler -> clientHandler.send(message));
    }

    @Override
    public void run() {
        boolean running = true;
        while (running && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}
