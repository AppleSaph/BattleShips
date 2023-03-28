package nl.applesaph.game;

import nl.applesaph.game.models.Player;
import nl.applesaph.game.models.Ship;
import nl.applesaph.game.models.ShipPart;
import nl.applesaph.server.SendCommand;
import nl.applesaph.server.Server;

import java.util.HashMap;
import java.util.Random;

public class Game {
    private final Server server;
    private final int[][] grid = new int[25][25];
    private final HashMap<Integer, Player> players = new HashMap<>();
    private GameState gameState = GameState.LOBBY;
    private int currentPlayer = 0;
    private int firstPlayer = 0;
    private int lastPlayer = 0;

    public Game(Server server) {
        this.server = server;
    }

    private void gameLoop() {

        if (gameState != GameState.RUNNING) return;
        int winner = checkWinner();

        if (winner != -1) {
            gameState = GameState.FINISHED;
            server.sendCommand(SendCommand.WINNER, "", winner);
            return;
        }

        server.sendCommand(SendCommand.TURN, "", currentPlayer);

    }

    private int checkWinner() {
        int amountOfPlayersAlive = 0;
        int winner = -1;
        for (Player player : players.values()) {
            if (!player.hasLost()) {
                amountOfPlayersAlive++;
                winner = player.getPlayerNumber();
            }
        }
        if (amountOfPlayersAlive == 1) {
            return winner;
        } else {
            return -1;
        }
    }

    private int changeTurn(int currentPlayer) {
        //if next player is not dead, return next player, wrap around if needed
        if (currentPlayer < lastPlayer) {
            //check if the next player exits
            if (players.get(currentPlayer + 1) != null && !players.get(currentPlayer + 1).hasLost()) {
                return currentPlayer + 1;
            } else {
                return changeTurn(currentPlayer + 1);
            }
        } else {
            if (players.get(firstPlayer) != null && !players.get(firstPlayer).hasLost()) {
                return firstPlayer;
            } else {
                firstPlayer++;
                return changeTurn(firstPlayer + 1);
            }
        }
    }

    public void initGrid() {
        Random random = new Random();
        for (Integer integer : players.keySet()) {
            Ship ship = new Ship();

            int x = random.nextInt(grid.length);
            int y = random.nextInt(grid[0].length);
            while (grid[x][y] != 0 || checkNeighbours(grid, x, y, 4)) {
                x = random.nextInt(grid.length);
                y = random.nextInt(grid[0].length);
            }
            int upOrDown = random.nextInt(2);
            //choose whether to place the ship vertically or horizontally
            if (upOrDown == 0) {
                //check if the ship is out of bounds, if so, move it back
                if (y + 2 >= grid[0].length) {
                    y -= 2;
                }
                if (y - 2 < 0) {
                    y += 2;
                }
                grid[x][y] = integer;
                grid[x][y + 1] = integer;
                grid[x][y + 2] = integer;
                ship.addShipPart(x, y);
                ship.addShipPart(x, y + 1);
                ship.addShipPart(x, y + 2);
            } else {
                //check if the ship is out of bounds, if so, move it back
                if (x + 2 >= grid.length) {
                    x -= 2;
                }
                if (x - 2 < 0) {
                    x += 2;
                }
                grid[x][y] = integer;
                grid[x + 1][y] = integer;
                grid[x + 2][y] = integer;
                ship.addShipPart(x, y);
                ship.addShipPart(x + 1, y);
                ship.addShipPart(x + 2, y);
            }
            players.get(integer).addShip(ship);
        }
    }

    public static boolean checkNeighbours(int[][] array, int x, int y, int n) {
        for (int i = x - n; i <= x + n; i++) {
            for (int j = y - n; j <= y + n; j++) {
                if (i >= 0 && i < array.length && j >= 0 && j < array[0].length && array[i][j] != 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public void addPlayer(int playerNumber, Player player) {
        players.put(playerNumber, player);
    }

    public void removePlayer(int playerNumber) {
        players.remove(playerNumber);
    }

    public void startGame() {
        initGrid();
        gameState = GameState.RUNNING;
        server.sendCommand(SendCommand.NEWGAME, grid[0].length + "~" + grid.length , 0);
        currentPlayer = players.keySet().iterator().next();
        firstPlayer = currentPlayer;
        for (Integer integer : players.keySet()) {
            for (Ship ship : players.get(integer).getShips()) {
                for (ShipPart part : ship.getShipParts()) {
                    server.sendCommand(SendCommand.POS, part.getX() + "~" + part.getY(), integer);
                }
            }
            if (integer > lastPlayer) {
                lastPlayer = integer;
            }
        }
        gameLoop();
        printGrid(grid);
    }

    public void printGrid(int[][] grid) {
        System.out.println("  0123456789111111111122222");
        System.out.println("            012345678901234");
        for (int y = 0; y < grid.length; y++) {
            if (y < 10) System.out.print("0");
            System.out.print(y);
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[x][y] == 0) {
                    System.out.print("_");
                } else {
                    System.out.print(grid[x][y]);
                }
            }
            System.out.println();
        }
    }

    public void endGame(Player winner) {
        gameState = GameState.FINISHED;
        server.sendCommand(SendCommand.WINNER, "", winner.getPlayerNumber());
        resetGame();
    }

    public boolean isTurn(int playerNumber) {
        return playerNumber == currentPlayer;
    }

    public void handleTurnMessage(int playerNumber, int x, int y) {
        if (isTurn(playerNumber)) {
            if (grid[x][y] != 0 && grid[x][y] != -1) {
                players.get(grid[x][y]).isHit(x, y);
                server.sendCommand(SendCommand.HIT, x + "~" + y, grid[x][y]);
                grid[x][y] = 0;
            } else {
                server.sendCommand(SendCommand.MISS, x + "~" + y, playerNumber);
                grid[x][y] = -1;
            }
            currentPlayer = changeTurn(currentPlayer);
            gameLoop();
            printGrid(grid);
        }
    }

    public void resetGame() {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                grid[x][y] = 0;
            }
        }
        players.clear();
        gameState = GameState.LOBBY;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void skipTurn() {
        if (gameState != GameState.RUNNING) throw new IllegalStateException("There is no game running!");
        currentPlayer = changeTurn(currentPlayer);
        gameLoop();
    }
}
