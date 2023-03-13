package nl.applesaph.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

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
    }

    private String parseIncomingMessage(String line) {
        return line;
    }

    private void close() throws IOException {
        server.removeClient(this);
        in.close();
        out.close();
        socket.close();
    }


    @Override
    public void run() {
        while (running) {
            try {
                String line = in.readLine();
                if (line != null) {
                    String message = parseIncomingMessage(line);
                    if (!message.equals("")) {
                        out.println(message);
                        out.flush();
                    }
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
