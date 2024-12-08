import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

public class Game {
    Queue<SnakeSegment> snake;
    TreeMap<Integer, Tile> tiles;
    Stack<BadFruit> fruit;
    int tilesPerRow;
    int gridSideLength;
    ArrayList<Tile> unoccupiedTiles;
    UI gameBoard;
    int headPosition;
    int score;

    public static void main(String[] args) {
        while (true) {
            new Game().start();
        }
    }

    public Game() {
        tiles = new TreeMap<>();
        unoccupiedTiles = new ArrayList<>();

        tilesPerRow = 20;
        gridSideLength = 1000;

        score = 0;

        // starts the UI
        gameBoard = new UI(gridSideLength, tilesPerRow);

        // sets the initial head position
        headPosition = tilesPerRow*tilesPerRow/2 + 4;

        // creates the initial game board and snake body
        snake = gameBoard.init(tiles, headPosition);
        
        generateUnoccupiedTiles(unoccupiedTiles, tiles);

        // generating the stack of fruit
        fruit = new Stack<>();
        gameBoard.generateFruit(fruit, unoccupiedTiles, tiles);
    }

    public void start() {
        // starts the game
        boolean gameRunning = true;
        boolean fruitEaten = false;
        while (gameRunning) {
            fruitEaten = false;
            gameBoard.startListening();
            generateUnoccupiedTiles(unoccupiedTiles, tiles);

            int[] directions = gameBoard.getPositionChanges();
            
            // waiting for the user to start moving
            if (directions[0] != 0 || directions[1] != 0) {
            
                // calculates the new head position based on the directions
                headPosition += directions[0] + directions[1]*tilesPerRow;

                // detecting crashes
                if (tiles.get(headPosition) instanceof OccupiedTile) {
                    // checks if the user wants to play again
                    if (gameBoard.gameOver(score)) {
                        // closes the window
                        gameBoard.close();
                        break;
                    } else {
                        System.exit(0);
                    }
                }

                // checks if there is a fruit at the next position
                if (tiles.get(headPosition) instanceof Fruit) {
                    fruitEaten = true;
                    // all of the fruit of the group have been eaten
                    if (fruit.isEmpty()) {
                        gameBoard.updateScore(++score);
                        generateUnoccupiedTiles(unoccupiedTiles, tiles);
                        gameBoard.generateFruit(fruit, unoccupiedTiles, tiles);
                    } else {
                        // making the next bad fruit edible
                        int nextFruitPosition = fruit.pop().getTileNum();
                        tiles.put(nextFruitPosition, new Fruit(nextFruitPosition));
                    }
                }

                // remove the tail when a fruit is not eaten
                if (!fruitEaten) {
                    // removing the tail;
                    SnakeSegment tail = snake.poll();

                    // removes the tail from the game board
                    tiles.put(tail.getTileNum(), new Tile(tail.getTileNum()));
                }

                // creates the new head
                SnakeSegment head = new SnakeSegment(headPosition);
                
                // adding the new head to the queue
                snake.add(head);

                // adding the new head to the board
                tiles.put(headPosition, head);
                gameBoard.drawTiles(tiles);

            }

            // time to wait in between board updates
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateUnoccupiedTiles(ArrayList<Tile> unoccupiedTiles, TreeMap<Integer, Tile> tiles) {
        unoccupiedTiles.clear();
        for (int i = 0; i < tiles.size(); i++) {
            if (!(tiles.get(i) instanceof OccupiedTile) && !(tiles.get(i) instanceof Fruit)) {
                unoccupiedTiles.add(tiles.get(i));
            }
        }
    }
}