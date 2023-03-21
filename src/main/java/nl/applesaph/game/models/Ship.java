package nl.applesaph.game.models;

import java.util.ArrayList;

public class Ship {

    private ArrayList<ShipPart> shipParts;

    public Ship() {
        shipParts = new ArrayList<>();
    }

    public boolean isHit(int x, int y){
        for (ShipPart shipPart : shipParts) {
            if (shipPart.getX() == x && shipPart.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void hitPart(int x, int y) {
        for (ShipPart shipPart : shipParts) {
            if (shipPart.getX() == x && shipPart.getY() == y) {
                shipPart.hit(true);
            }
        }
    }

    public void addShipPart(int x, int y) {
        shipParts.add(new ShipPart(x, y,false));
    }
}
