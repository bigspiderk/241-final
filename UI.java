import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class UI {
    private JFrame frame;
    private JPanel panel;
    private int dx;
    private int dy;
    private int tilesPerRow;
    private boolean listening;

    public UI(int gridSideLength, int tilesPerRow) {
        // sets the change in positions to 0
        dx = 0;
        dy= 0;

        listening = true;

        this.tilesPerRow = tilesPerRow;

        frame = new JFrame("Score: 0");

        // Set the size of the frame
        frame.setSize(gridSideLength, gridSideLength);

        // Set the default close operation to exit the application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // make sure the frame cannot be resized
        frame.setResizable(false);
        
        // Set the layout to a grid
        panel = new JPanel();
        panel.setLayout(new GridLayout(tilesPerRow, tilesPerRow));

        frame.add(panel);

        // Add key listener for WASD keys
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!listening) {
                    return;
                }
                // Check for W, A, S, D key presses
                // sets the position change based on the button pressed
                if (e.getKeyCode() == KeyEvent.VK_W && dy != -1) {
                    // when W is pressed moves up if not moving down
                    dx = 0;
                    dy = -1;
                    listening = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_S && dy != 1) {
                    // when S is pressed moves down if not moving up
                    dx = 0;
                    dy = 1;
                    listening = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_A && dx != 1) {
                    // when A is pressed moves left if not moving right
                    dx = -1;
                    dy = 0;
                    listening = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_D && dx != -1) {
                    // when D is pressed moves right if not moving left
                    dx = 1;
                    dy = 0;
                    listening = false;
                }
            }
        });


        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Make the frame visible
        frame.setVisible(true);
    }

    // creates the initial gameboard with the floor tiles
    // returns a queue of the snake body parts with the tail at the end
    public Queue<SnakeSegment> init(TreeMap<Integer, Tile> tiles, int headPosition) {
        // adding the top and bottom wall
        for (int i = 0; i < tilesPerRow; i++) {
            tiles.put(i, new Wall(i));
            tiles.put(tilesPerRow*(tilesPerRow - 1) + i, new Wall(tilesPerRow*(tilesPerRow - 1) + i));
        }

        for (int i = tilesPerRow; i < tilesPerRow*(tilesPerRow - 1); i++) {
            // adding the wall to the first and last tile in each row
            if (i % tilesPerRow == 0 || i % tilesPerRow == tilesPerRow-1) {
                tiles.put(i, new Wall(i));
                continue;
            }

            tiles.put(i, new Tile(i));
        }

        Queue<SnakeSegment> snake = new LinkedList<>();

        // adds the initial snake body to the board
        for (int i = 1; i < 3; i++) {
            SnakeSegment segment = new SnakeSegment(headPosition-3+i);
            tiles.put(headPosition-3+i, segment);
            snake.add(segment);
        }

        // adding the head
        SnakeHead head = new SnakeHead(headPosition);
        snake.add(head);
        tiles.put(headPosition, head);

        drawTiles(tiles);
        return snake;
    }

    public void drawTiles(TreeMap<Integer, Tile> tiles) {
        // adds the tiles from the game board to the UI and update UI
        panel.removeAll();;
        for (int i = 0; i < tilesPerRow*tilesPerRow; i++) {
            panel.add(tiles.get(i));
        }
        panel.revalidate();
        panel.repaint();
    }

    public int[] getPositionChanges() {
        return new int[]{dx, dy};
    }

    public void generateFruit(Stack<BadFruit> fruit, ArrayList<Tile> unoccupiedTiles, TreeMap<Integer, Tile> tiles) {
        // generating the number of fruit to be added
        int numBadFruit = (int) ((Math.random() * (7 - 1)) + 1);

        // adding the bad fruit to the stack
        for (int i = 0; i < numBadFruit; i++) {
            // the random unoccupied tile that the fruit will be added to
            int tileChoice = (int) ((Math.random() * unoccupiedTiles.size()));
            // adding the fruit to the stack
            fruit.add(new BadFruit(unoccupiedTiles.get(tileChoice).getTileNum()));
            // adding it to the game board
            tiles.put(fruit.peek().getTileNum(), fruit.peek());
            // make sure the tile is no longer marked as unoccupied
            unoccupiedTiles.remove(tileChoice);
        }

        // setting the first bad fruit to an edible fruit
        BadFruit firstFruit = fruit.pop();
        tiles.put(firstFruit.getTileNum(), new Fruit(firstFruit.getTileNum()));

        drawTiles(tiles);
    }

    public void startListening() {
        listening = true;
    }

    public void updateScore(int score) {
        frame.setTitle("Score: " + score);
    }

    public boolean gameOver(int score) {
        // prompts the user to play again
        String[] options = new String[]{"Play Again", "Quit"};
        int choice = JOptionPane.showOptionDialog(
                null, 
                String.format("Score: %d\nPlay Again?", score),
                "Game Over",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null,
                options,
                options[1]
        );
        return choice == 0;
    }

    public void close() {
        frame.dispose();
    }

}

// Tile class extending JPanel
class Tile extends JPanel {
    private int tileNum;

    public Tile(int tileNum) {

        this.tileNum = tileNum;

        // Set the background color
        setBackground(Color.BLACK);

        // add a white border
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

    }

    // returns the position of the tile
    public int getTileNum() {
        return tileNum;
    }
}

// class for tiles that are occupied to calculate crashes
class OccupiedTile extends Tile {
    public OccupiedTile(int tileNum) {
        super(tileNum);
    }
}

class SnakeSegment extends OccupiedTile {
    public SnakeSegment(int tileNum) {
        super(tileNum);
        // Set the background color to Green
        setBackground(Color.GREEN);
    }
}

class SnakeHead extends SnakeSegment {
    // snake head is a segment with a direction arrow
    public SnakeHead(int tileNum) {
        super(tileNum);
        // initial arrow is to the right
        add(new JLabel(("→")));
    }

    public SnakeHead(int tileNum, String directionArrow) {
        super(tileNum);
        // sets the arrow in the JLabel
        add(new JLabel(directionArrow));
    }
}

// class for bad fruit, ends the game if user moves onto it
class BadFruit extends OccupiedTile {
    public BadFruit(int tileNum) {
        super(tileNum);
        // Set the background color to Purple
        setBackground(new Color(148,0,211));
    }
}

// class for fruit, user gets a point after eating a group of fruits
class Fruit extends Tile {
    public Fruit(int tileNum) {
        super(tileNum);
        // Set the background color to Red
        setBackground(Color.RED);
    }
}

// class for Wall, ends the game if the user crashes into it
class Wall extends OccupiedTile {
    public Wall(int tileNum) {
        super(tileNum);

        // Set the background color to white
        setBackground(Color.GRAY);
    }
}