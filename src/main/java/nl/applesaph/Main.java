package nl.applesaph;

import nl.applesaph.server.Server;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static boolean exit = false;

    public static void main(String[] args) {
        int port = 0;
        int argsPort = 0;
        if (args.length > 0) {
            try {
                argsPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {

            }
        }

        Scanner scanner = new Scanner(System.in);
        boolean valid;
        do {
            try {
                if (argsPort != 0) {
                    port = argsPort;
                    System.out.println("Using port " + port);
                    break;
                } else {
                    System.out.println("Please enter a port number (or leave empty for 55555):");
                    String input = scanner.nextLine();
                    valid = true;
                    if (input.equals("")) {
                        //default port if input is empty
                        port = 55555;
                        break;
                    }
                    port = Integer.parseInt(input);
                }
            } catch (NumberFormatException e) {
                //if input isn't a number
                System.err.println("ERROR: Not a valid port");
                valid = false;
            }
            if (!(0 <= port && port <= 65536)) {
                //if input is out of range
                System.err.println("ERROR: Port number is invalid");
                valid = false;
            }
        } while (!valid);
        Server server = new Server(port);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Type 'START' to start the game, 'SKIP' to skip a turn and 'QUIT' to quit the server");
        while (!exit) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                exit = true;
            } else if (input.equalsIgnoreCase("start")) {
                try {
                    server.startGame();
                    System.out.println("Game started");
                } catch (IllegalStateException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            } else if (input.equalsIgnoreCase("skip")) {
                try {
                    server.skipTurn();
                    System.out.println("Turn skipped");
                } catch (IllegalStateException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            }
        }
        server.stop();
    }
}