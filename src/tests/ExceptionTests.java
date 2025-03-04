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
            board.setConfigFiles("ClueLayoutBadColumns.csv", "ClueSetup.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }

    @Test
    public void testBadRoom() throws BadConfigFormatException, FileNotFoundException {
        assertThrows(BadConfigFormatException.class, () -> {
            Board board = Board.getInstance();
            board.setConfigFiles("ClueLayoutBadRoom.csv", "ClueSetup.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }

    @Test
    public void testBadRoomFormat() throws BadConfigFormatException, FileNotFoundException {
        assertThrows(BadConfigFormatException.class, () -> {
            Board board = Board.getInstance();
            board.setConfigFiles("ClueLayout.csv", "ClueSetupBadFormat.txt");
            board.loadSetupConfig();
            board.loadLayoutConfig();
        });
    }
}
