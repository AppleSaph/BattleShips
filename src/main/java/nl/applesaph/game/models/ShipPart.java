package nl.applesaph.game.models;

public class ShipPart {

    private final int x;
    private final int y;
    private boolean isHit;

    public ShipPart(int x, int y, boolean isHit) {
        this.x = x;
        this.y = y;
        this.isHit = isHit;
    }

    public ShipPart(int x, int y) {
        this.x = x;
        this.y = y;
        this.isHit = false;
    }

    public void hit(boolean b) {
        isHit = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHit() {
        return isHit;
    }
}
