package nl.applesaph.game.models;

import java.util.ArrayList;

public class Player {

    private int playerNumber;
    private String playerName;
    private ArrayList<Ship> ships = new ArrayList<>();

    public Player(int playerNumber, String playerName) {
        this.playerNumber = playerNumber;
        this.playerName = playerName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public boolean isHit(int x, int y) {
        for (Ship ship : ships) {
            if (ship.isHit(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public boolean hasLost() {
        for (Ship ship : ships) {
            if (!ship.hasSunk()) {
                return false;
            }
        }

        return true;
    }

}
