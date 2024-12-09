import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class GameTest {
    private Game game;

    @Before
    public void init() {
        // generate a new game for each test
        game = new Game();
    }

    @Test
    public void testSnakeBodySize() {
        // check that the initial size of the snake is 3
        assertEquals(3, game.snake.size());
    }

    @Test
    public void testTilesSize() {
        // check that the game board is 20x20
        assertEquals(400, game.tiles.size());
    }

    @Test
    public void testFruitStackSize() {
        // checks that 1 to 7 fruit were generated
        assertTrue(game.fruit.size() >= 1 && game.fruit.size() <= 7);
    }

    @Test
    public void checkTailPosition() {
        // checks that the position of the tail is tile 202
        assertEquals(202, game.snake.peek().getTileNum());
    }

    @Test
    public void checkScoreIncrease() {
        // setting the tile in front of the snake head to a fruit
        game.tiles.put(205, new Fruit(205));
        // moving the snake 1 tile to the right so it should eat 
        // the fruit in front of it
        game.tick(new int[]{1,0});
        // the score should now equal 1
        assertEquals(1, game.score);
    }

    @Test
    public void checkCrash() {
        // setting the tile in front of the snake head to a wall
        game.tiles.put(205, new Wall(205));
        // moving the snake 1 tile to the right so it should crash 
        game.tick(new int[]{1,0});
        // game over variable should be marked to true
        assertTrue(game.gameOver);
    }
}
