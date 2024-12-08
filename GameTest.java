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
    public void checkHeadPosition() {
        // checks that the position of the head is tile 202
        assertEquals(202, game.snake.peek().getTileNum());
    }
}
