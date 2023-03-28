package nl.applesaph.server;

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

    private final int port;
    private final Game game = new Game(this);
    private ServerSocket serverSocket;
    private Thread serverThread;
    private final HashMap<Integer, ClientHandler> clientHandlers = new HashMap<>();
    private final HashMap<Integer, String> usernames = new HashMap<>();

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
    public void handleTurnMessage(ClientHandler client, int x, int y) {
        if (game.isTurn(client.getPlayerNumber())) {
            game.handleTurnMessage(client.getPlayerNumber(), x, y);

        }
    }

    public void skipTurn() {
        game.skipTurn();
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
        if (clientHandlers.containsKey(playerNumber)) {
            clientHandlers.get(playerNumber).send(message);
        }
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

    public synchronized String getUsername(int playerNumber) {
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
                if (playerNumber != -1) {
                    ClientHandler clientHandler = new ClientHandler(socket, this, playerNumber);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                    clientHandlers.replace(playerNumber, clientHandler);
                    continue;
                } else {
                    playerNumber = usernames.size() + 1;
                    usernames.put(playerNumber, username);
                }
                ClientHandler clientHandler = new ClientHandler(socket, this, playerNumber);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
                clientHandlers.put(playerNumber, clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public void handleCommand(ReceiveCommand command, ClientHandler clientHandler, String line) {
        switch (command) {
            case EXIT -> game.removePlayer(clientHandler.getPlayerNumber());
            case MOVE -> {
                if (!tryParse(line.split("~")[1]) && !tryParse(line.split("~")[2])) {
                    sendCommand(SendCommand.ERROR, "Invalid move", clientHandler.getPlayerNumber());
                    break;
                }
                if (!game.isTurn(clientHandler.getPlayerNumber())) {
                    sendCommand(SendCommand.ERROR, "Not your turn", clientHandler.getPlayerNumber());
                    break;
                }
                if (Integer.parseInt(line.split("~")[1]) < 0 || Integer.parseInt(line.split("~")[1]) > game.getGrid().length || Integer.parseInt(line.split("~")[2]) < 0 || Integer.parseInt(line.split("~")[2]) > game.getGrid()[0].length) {
                    sendCommand(SendCommand.ERROR, "X and Y need to be between 0 and " + game.getGrid().length, clientHandler.getPlayerNumber());
                    break;
                }
                handleTurnMessage(clientHandler, Integer.parseInt(line.split("~")[1]), Integer.parseInt(line.split("~")[2]));
            }
            case PING -> clientHandler.send("PONG");
            case PONG -> clientHandler.setLastPong(System.currentTimeMillis());
            case NEWGAME -> {
                startGame();
            }
            default -> {
            }
        }
    }
    public void startGame() {
        game.resetGame();
        clientHandlers.forEach((key, value) -> game.addPlayer(key, new Player(key, getUsername(key))));
        game.startGame();
    }

    private boolean tryParse(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void sendCommand(SendCommand command, String message, int player) {
        switch (command) {
            case HIT -> sendToAll("HIT~" + message + "~" + player);
            case MISS -> sendToAll("MISS~" + message);
            case WINNER -> sendToAll("WINNER~" + player);
            case LOST -> sendToAll("LOST~" + player);
            case ERROR -> sendToClient("ERROR~" + message, player);
            case EXIT -> sendToAll("EXIT");
            case TURN -> sendToAll("TURN~" + player);
            case NEWGAME -> sendToAll("NEWGAME" + player);
            case PING -> sendToClient("PING", player);
            case PONG -> sendToClient("PONG", player);
            case HELLO -> sendToClient("HELLO~" + message, player);
            case POS -> sendToClient("POS~" + message, player);
        }
    }
}
