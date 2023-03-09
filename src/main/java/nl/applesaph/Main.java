package nl.applesaph;

import java.util.Scanner;

public class Main {

    private static boolean exit = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean valid;
        int port = 0;
        do {
            System.out.println("Please enter a port number (or leave empty for 55555):");
            valid = true;
            try {
                String input = scanner.nextLine();
                if (input.equals("")) {
                    //default port if input is empty
                    port = 55555;
                    break;
                }
                port = Integer.parseInt(input);
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
        Server s = new Server(port);
        s.start();
        while (!exit) {
            if (scanner.nextLine().equals("quit")) {
                exit = true;
            }
        }
        s.stop();
    }
}