package tests;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/*
 * Created by Nick Silzell and Andrew Grimes, tests setup of our Clue map
 */
public class FileInitTests {
	// Constants used to test proper loading
	public static final int NUM_ROWS = 32;
	public static final int NUM_COLUMNS = 32;
	public static final int NUM_DOORS = 18;
	private static Board board;

	// Use the same copy of Board
	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();

		// Setup files and initialize board based on them
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
	}

	@Test
	public void testRoomLabels() {
		// Make sure rooms are properly retrieved, with the first, last, and some from middle
		assertEquals("Town Hall", board.getRoom('T').getName());
		assertEquals("Gold Storage", board.getRoom('G').getName());
		assertEquals("Clan Castle", board.getRoom('C').getName());
		assertEquals("Spell Factory", board.getRoom('S').getName());
		assertEquals("Laboratory", board.getRoom('L').getName());
	}

	@Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	/*
	 * Test a doorway in each direction, one walkway cell,
	 * one cell in the middle of the room, and one wall cell
	 */
	@Test
	public void TestDoorDirections() {
		BoardCell cell = board.getCell(23, 25);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
		cell = board.getCell(8, 7);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
		cell = board.getCell(12, 9);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
		cell = board.getCell(20, 23);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());

		// Test non doorway cells
		cell = board.getCell(15, 16);
		assertFalse(cell.isDoorway());
		cell = board.getCell(5, 4);
		assertFalse(cell.isDoorway());
		cell = board.getCell(0, 0);
		assertFalse(cell.isDoorway());
	}

	/*
	 * make sure there is the correct number of doorways
	 */
	@Test
	public void testNumDoors(){
		int doors = 0;
		for (int row = 0; row < board.getNumRows(); row++) {
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if(cell.isDoorway()) doors++;
			}
		}
		assertEquals(NUM_DOORS, doors);
	}

	/*
	 * Test all types of rooms:
	 * Normal room
	 * Label Cell
	 * Room center
	 * Secret passage
	 */
	@Test
	public void TestTypesOfRooms() {
		// Normal Room
		BoardCell cell = board.getCell(15,16);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Town Hall");
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isDoorway());

		// Label Cell
		cell = board.getCell(9, 5);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Army Camp");
		assertTrue(cell.isLabel());
		assertFalse(cell.isRoomCenter());

		// Room center
		cell = board.getCell(20, 25);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Dark Barracks");
		assertFalse(cell.isLabel());
		assertTrue(cell.isRoomCenter());

		// Secret passage
		cell = board.getCell(28, 19);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Elixir Storage");
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertEquals('G', cell.getSecretPassage());
	}
	
	
	/*
	 * Test and make sure all four corners are unused
	 */
	@Test
	public void TestUnusedCorners() {
		BoardCell cell = board.getCell(0, 0);
		assertEquals(cell.getInitial(), 'X');
		cell = board.getCell(NUM_ROWS - 1, 0);
		assertEquals(cell.getInitial(), 'X');
		cell = board.getCell(NUM_ROWS - 1, NUM_COLUMNS - 1);
		assertEquals(cell.getInitial(), 'X');
		cell = board.getCell(0, NUM_COLUMNS - 1);
		assertEquals(cell.getInitial(), 'X');
	}
}
