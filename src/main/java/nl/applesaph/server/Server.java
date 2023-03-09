package nl.applesaph.server;

import java.util.List;

public class Server implements ServerInterface{
    public Server(int port) {
        
    }

    public void start() {
        
    }

    public void stop() {
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void newGame(List<ClientHandler> clientHandlers) {

    }

    @Override
    public boolean inGame(ClientHandler clientHandler) {
        return false;
    }

    @Override
    public void handleTurnMessage(ClientHandler client, String message) {

    }

    @Override
    public void handleSetupTurnMessage(ClientHandler client, String message) {

    }

    @Override
    public boolean checkUsernameLoggedIn(String username) {
        return false;
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
    public void removeClient(ClientHandler clientHandler) {

    }

    @Override
    public void addClient(ClientHandler clientHandler) {

    }

    @Override
    public void sendToAll(String message) {

    }

    @Override
    public void sendToAllExcept(String message, ClientHandler clientHandler) {

    }

    @Override
    public void sendToClient(String message, ClientHandler clientHandler) {

    }

    @Override
    public void sendToClients(String message, List<ClientHandler> clientHandlers) {

    }
}
