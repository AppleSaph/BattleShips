package nl.applesaph.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final int playerNumber;
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private Server server;
    private boolean running;

    public ClientHandler(Socket socket, Server server, int playerNumber) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.running = true;
        this.server = server;
        this.playerNumber = playerNumber;
        server.addClient(playerNumber,this);
        System.out.println("[CONNECT] " + socket.getInetAddress() + ":" + socket.getPort() + " with username " + server.getUsername(playerNumber) + " [" + playerNumber + "]");
    }

    private void parseIncomingMessage(String line) {
        if (!line.equals("")) {
            System.out.println("[" + playerNumber + "] " + line);
            send(line);
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
        if(!running){
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

    public int getPlayerNumber(){
        return playerNumber;
    }
}
