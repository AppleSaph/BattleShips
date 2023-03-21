package nl.applesaph.game;

import java.util.HashMap;
import java.util.Random;

public class Game {

    private int grid[][] = new int[25][25];
    private HashMap<Integer, Player> players = new HashMap<>();
    private GameState gameState = GameState.LOBBY;

    private void initGrid() {
        Random random = new Random();
        for (Integer integer : players.keySet()) {

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
            }
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
    }

    public void printGrid() {
        for (int y = 0; y < grid.length; y++) {
            for (int[] ints : grid) {
                if (ints[y] == 0) {
                    System.out.print("_");
                } else {
                    System.out.print(ints[y]);
                }
            }
            System.out.println();
        }
    }

    public void endGame() {
        gameState = GameState.FINISHED;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
