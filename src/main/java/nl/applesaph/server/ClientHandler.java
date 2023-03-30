package nl.applesaph.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final int playerNumber;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Server server;
    private boolean running;
    private long lastPong;

    public ClientHandler(Socket socket, Server server, int playerNumber) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.running = true;
        this.server = server;
        this.playerNumber = playerNumber;
        server.addClient(playerNumber, this);
        System.out.println("[CONNECT] " + socket.getInetAddress() + ":" + socket.getPort() + " with username " + server.getUsername(playerNumber) + " [" + playerNumber + "]");
        try {
            Thread.sleep(100);
            server.sendCommand(SendCommand.HELLO, Integer.toString(playerNumber), playerNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseIncomingMessage(String line) throws IOException {
        if (!line.equals("")) {
            switch (line.split("~")[0]) {
                case "EXIT" -> {
                    server.handleCommand(ReceiveCommand.EXIT, this, line);
                    close();
                }
                case "MOVE" -> server.handleCommand(ReceiveCommand.MOVE, this, line);
                case "PING" -> server.handleCommand(ReceiveCommand.PING, this, line);
                case "PONG" -> server.handleCommand(ReceiveCommand.PONG, this, line);
                case "NEWGAME" -> server.handleCommand(ReceiveCommand.NEWGAME, this, line);
                default -> server.sendToClient("ERROR~Invalid command", playerNumber);
            }

        }
    }

    private void close() throws IOException {
        System.out.println("[DISCONNECT] " + socket.getInetAddress() + ":" + socket.getPort() + " with username " + server.getUsername(playerNumber) + " [" + playerNumber + "]");
        server.removeClient(playerNumber);
        in.close();
        out.close();
        socket.close();
    }

    protected void send(String message) {
        //send message to client
        if (!running) {
            throw new IllegalStateException("Not running");
        }
        if (!socket.isConnected()) {
            throw new IllegalStateException("Socket is not connected");
        }
        out.println(message);
        out.flush();
    }


    @Override
    public void run() {
        while (running) {
            try {
                String line = in.readLine();
                if (line != null) {
                    parseIncomingMessage(line);
                } else {
                    running = false;
                }
            } catch (IOException e) {
                running = false;
            }
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setLastPong(long lastPong) {
        this.lastPong = lastPong;
    }

    public long getLastPong() {
        return lastPong;
    }

    public Socket getSocket() {
        return socket;
    }
}
