package nl.applesaph.game.models;

import java.util.ArrayList;

public class Ship {

    private final ArrayList<ShipPart> shipParts;

    public Ship() {
        shipParts = new ArrayList<>();
    }

    public boolean isHit(int x, int y) {
        for (ShipPart shipPart : shipParts) {
            if (shipPart.getX() == x && shipPart.getY() == y) {
                shipPart.hit(true);
                return true;
            }
        }
        return false;
    }


    public boolean hasSunk() {
        for (ShipPart shipPart : shipParts) {
            if (!shipPart.isHit()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ShipPart> getShipParts() {
        return shipParts;
    }

    public void addShipPart(int x, int y) {
        shipParts.add(new ShipPart(x, y, false));
    }
}
