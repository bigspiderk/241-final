import java.util.ArrayList;
import java.util.HashMap;
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
    HashMap<Integer, String> directionArrows;
    boolean gameOver;

    public static void main(String[] args) {
        // creates a new window every round until the program is exited
        while (true) {
            new Game().start();
        }
    }

    public Game() {
        tiles = new TreeMap<>();
        unoccupiedTiles = new ArrayList<>();
        directionArrows = new HashMap<>();

        tilesPerRow = 20;
        gridSideLength = 1000;

        score = 0;

        gameOver = false;

        // starts the UI
        gameBoard = new UI(gridSideLength, tilesPerRow);

        // sets the initial head position
        headPosition = tilesPerRow*tilesPerRow/2 + 4;

        // creates the initial game board and snake body
        snake = gameBoard.init(tiles, headPosition);
        
        generateUnoccupiedTiles(unoccupiedTiles, tiles);

        // setting the arrows to the correstponding number based on the
        // x and y position changes
        directionArrows.put(1, "→");
        directionArrows.put(-1, "←");
        directionArrows.put(tilesPerRow, "↓");
        directionArrows.put(-tilesPerRow, "↑");

        // generating the stack of fruit
        fruit = new Stack<>();
        gameBoard.generateFruit(fruit, unoccupiedTiles, tiles);
    }

    public void start() {
        // starts the game
        boolean gameRunning = true;

        while (gameRunning) {
            // generating the direction changes based on the key pressed
            int[] directions = gameBoard.getPositionChanges();
            // simulating a turn of the game
            gameRunning = tick(directions);

            try {
                // time to wait in between board updates
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean tick(int[] directions) {
        boolean fruitEaten = false;
        // make the UI start listening for key presses
        gameBoard.startListening();
        // getting the list of tile that are not occupied
        generateUnoccupiedTiles(unoccupiedTiles, tiles);
        
        // waiting for the user to start moving
        if (directions[0] != 0 || directions[1] != 0) {

            // setting the current head to a snake segment instead of a head
            // to remove the direction arrow
            tiles.put(headPosition, new SnakeSegment(headPosition));
        
            // calculates the new head position based on the directions
            headPosition += directions[0] + directions[1]*tilesPerRow;

            // finds the correct arrow based on the x and y changes
            String arrow = directionArrows.get(directions[0]+directions[1]*tilesPerRow);

            // detecting crashes
            if (tiles.get(headPosition) instanceof OccupiedTile) {
                // ending the game
                gameOver = true;
                // checks if the user wants to play again
                if (gameBoard.gameOver(score)) {
                    // closes the window
                    gameBoard.close();
                    // returning false so the current game ends
                    // and a new one is made
                    return false;
                } else {
                    // exits the program
                    System.exit(0);
                }
            }

            // checks if there is a fruit at the next position
            if (tiles.get(headPosition) instanceof Fruit) {
                fruitEaten = true;
                // increasing the score when a fruit is eaten
                gameBoard.updateScore(++score);
                // all of the fruit of the group have been eaten
                if (fruit.isEmpty()) {
                    // generates new fruit based on the unoccupied tiles
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
            SnakeHead head = new SnakeHead(headPosition, arrow);
            
            // adding the new head to the queue
            snake.add(head);

            // adding the new head to the board
            tiles.put(headPosition, head);

            // updating the game board
            gameBoard.drawTiles(tiles);
        }

        // the game continues if nothing happens or if the user doesnt lose
        return true;
    }

    public static void generateUnoccupiedTiles(ArrayList<Tile> unoccupiedTiles, TreeMap<Integer, Tile> tiles) {
        unoccupiedTiles.clear();
        for (int i = 0; i < tiles.size(); i++) {
            // adding all the tiles that are not occupied to the list
            if (!(tiles.get(i) instanceof OccupiedTile) && !(tiles.get(i) instanceof Fruit)) {
                unoccupiedTiles.add(tiles.get(i));
            }
        }
    }
}