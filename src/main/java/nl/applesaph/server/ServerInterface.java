package nl.applesaph.server;

import java.util.List;

public interface ServerInterface {

    void start();

    void stop();

    int getPort();

    void newGame(List<ClientHandler> clientHandlers);

    boolean inGame(ClientHandler clientHandler);

    void handleTurnMessage(ClientHandler client, String message);

    void handleSetupTurnMessage(ClientHandler client, String message);

    boolean checkUsernameLoggedIn(String username);

    List<ClientHandler> getLoggedInClients();
    
    List<ClientHandler> getWaitingClients();

    void removeClient(ClientHandler clientHandler);

    void addClient(ClientHandler clientHandler);

    void sendToAll(String message);

    void sendToAllExcept(String message, ClientHandler clientHandler);

    void sendToClient(String message, ClientHandler clientHandler);

    void sendToClients(String message, List<ClientHandler> clientHandlers);


}
