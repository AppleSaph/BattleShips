package nl.applesaph;

import nl.applesaph.server.Server;

import java.util.Scanner;

public class Main {

    private static boolean exit = false;

    public static void main(String[] args) {
        int port = 0;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {

            }
        }

        Scanner scanner = new Scanner(System.in);
        boolean valid;
        do {
            try {
                if (port != 0) {
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
                System.out.println("ERROR: Not a valid port");
                valid = false;
            }
            if (!(0 <= port && port <= 65536)) {
                //if input is out of range
                System.out.println("ERROR: Port number is invalid");
                valid = false;
            }
        } while (!valid);
        Server server = new Server(port);
        server.start();
        while (!exit) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                exit = true;
            } else if (input.equalsIgnoreCase("skip")) {
                server.skipTurn();
            }
        }
        server.stop();
    }
}