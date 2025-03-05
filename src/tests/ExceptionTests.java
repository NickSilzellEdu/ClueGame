package tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import clueGame.BadConfigFormatException;
import clueGame.Board;
import java.io.FileNotFoundException;

public class ExceptionTests {

    @Test
    public void testBadColumns() throws BadConfigFormatException, FileNotFoundException {
        assertThrows(BadConfigFormatException.class, () -> {
            Board board = Board.getInstance();
            board.setConfigFiles("data/ClueLayoutBadColumns.csv", "data/ClueSetup.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }

    @Test
    public void testBadRoom() throws BadConfigFormatException, FileNotFoundException {
        assertThrows(BadConfigFormatException.class, () -> {
            Board board = Board.getInstance();
            board.setConfigFiles("data/ClueLayoutBadRoom.csv", "data/ClueSetup.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }

    @Test
    public void testBadRoomFormat() throws BadConfigFormatException, FileNotFoundException {
        assertThrows(BadConfigFormatException.class, () -> {
            Board board = Board.getInstance();
            board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetupBadFormat306.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }
}
