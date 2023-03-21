package nl.applesaph.server;

import nl.applesaph.Main;
import nl.applesaph.game.Game;
import nl.applesaph.game.models.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Server implements ServerInterface, Runnable {

    private int port;
    private Game game = new Game(this);
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
        if(game.isTurn(client.getPlayerNumber())){
            game.handleTurnMessage(client.getPlayerNumber(), message);

        }
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
    public synchronized void removeClient(int playerNumber) {
        clientHandlers.remove(playerNumber);
    }

    @Override
    public synchronized void addClient(int playerNumber, ClientHandler clientHandler) {
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

    public synchronized String getUsername(int playerNumber){
        return usernames.get(playerNumber);
    }

    @Override
    public void run() {
        boolean running = true;
        while (running && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                //check for the first message, this should be the username
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String username = in.readLine();
                int playerNumber = checkUsernameLoggedIn(username);
                if(playerNumber != -1){
                    ClientHandler clientHandler = new ClientHandler(socket, this, playerNumber);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                    clientHandlers.replace(playerNumber, clientHandler);
                    continue;
                } else {
                    playerNumber = usernames.size() + 1;
                    usernames.put(playerNumber, username);
                    game.addPlayer(playerNumber, new Player(playerNumber,username));
                }
                ClientHandler clientHandler = new ClientHandler(socket, this, playerNumber);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public void handleCommand(ReceiveCommand command, ClientHandler clientHandler, String line) {
        switch (command){
            case EXIT:
                game.removePlayer(clientHandler.getPlayerNumber());
                break;
            case MOVE:
                handleTurnMessage(clientHandler, line);
                break;
            case PING:
                clientHandler.send("PONG");
                break;
            case PONG:
                clientHandler.setLastPong(System.currentTimeMillis());
                break;
            default:
                break;
        }
    }

    public void sendCommand(SendCommand command, String line, int currentPlayer) {
        switch (command){
            case HIT:
                sendToAll("HIT~" + line + "~" + currentPlayer);
            case MISS:
                sendToAll("MISS~" + line + "~" + currentPlayer);
            case WINNER:
                sendToAll("WINNER~" + currentPlayer);
            case LOST:
                sendToAll("LOST~" + currentPlayer);
            case ERROR:
                sendToAll("ERROR~" + line);
            case EXIT:
                sendToAll("EXIT");
            case TURN:
                sendToAll("TURN~" + currentPlayer);
            case NEWGAME:
                sendToAll("NEWGAME" + currentPlayer);
            case PING:
                sendToClient("PING", currentPlayer);
            case PONG:
                sendToClient("PONG", currentPlayer);
        }
    }
}
