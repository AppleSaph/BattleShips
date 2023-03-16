package nl.applesaph.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private Server server;
    private boolean running;
    private String logInName = null;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.running = true;
        this.server = server;
        server.addClient(this);
        System.out.println("[CONNECT] " + socket.getInetAddress() + ":" + socket.getPort());
        send("Welcome to the BattleShips server!");
    }

    private void parseIncomingMessage(String line) {
        if (!line.equals("")) {
            send(line);
        }
    }

    private void close() throws IOException {
        System.out.println("[DISCONNECT] " + socket.getInetAddress() + ":" + socket.getPort());
        server.removeClient(this);
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
}
