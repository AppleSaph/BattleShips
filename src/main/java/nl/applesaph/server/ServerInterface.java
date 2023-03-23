package nl.applesaph.server;

import java.util.List;

public interface ServerInterface {

    void start();

    void stop();

    int getPort();

    void newGame(List<ClientHandler> clientHandlers);

    boolean inGame(ClientHandler clientHandler);

    void handleTurnMessage(ClientHandler client, int x, int y);

    void handleSetupTurnMessage(ClientHandler client, String message);

    int checkUsernameLoggedIn(String username);

    List<ClientHandler> getLoggedInClients();

    List<ClientHandler> getWaitingClients();

    void removeClient(int playerNumber);

    void addClient(int PlayerNumber, ClientHandler clientHandler);

    void sendToAll(String message);

    void sendToAllExcept(String message, int[] playerNumbers);

    void sendToClient(String message, int playerNumber);

    void sendToClients(String message, int[] playerNumbers);


}
