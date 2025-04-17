package tests;
/*
 * Made by Nick Silzell and Andrew grimes to test adjacencies and targets of our board
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {
	private static Board board;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
	}

	// Make sure player does not move around within room
	// Cells are light orange on spreadsheet
	@Test
	public void testAdjacenciesRooms() {
		// Test Gold storage, it has two entrances and a secret passage
		Set<BoardCell> testList = board.getAdjList(5, 9);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(7, 9)));
		assertTrue(testList.contains(board.getCell(5, 13)));
		assertTrue(testList.contains(board.getCell(26, 21)));

		// Test Clan Castle, it only has one entrance
		testList = board.getAdjList(20, 5);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(20, 8)));

		// Test Laboratory, a room with two entrances
		testList = board.getAdjList(25, 8);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(25,12)));
		assertTrue(testList.contains(board.getCell(22, 10)));
	}

	// Test door locations include their rooms and walkways around them
	// Cells are light orange on spreadsheet
	@Test
	public void testAdjacenciesDoors() {
		// Test door into Spell Factory, with a room and walkways around it
		Set<BoardCell> testList = board.getAdjList(12, 25);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(10, 26)));
		assertTrue(testList.contains(board.getCell(12, 24)));
		assertTrue(testList.contains(board.getCell(13, 25)));

		// Test door into Builder Hut, 3 adjacent walkways and one room
		testList = board.getAdjList(7, 20);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(8, 20)));
		assertTrue(testList.contains(board.getCell(7, 19)));
		assertTrue(testList.contains(board.getCell(7, 21)));
		assertTrue(testList.contains(board.getCell(5, 20)));

		// Test door into Clan Castle, 3 adjacent walkways and one room
		testList = board.getAdjList(20, 8);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(20, 9)));
		assertTrue(testList.contains(board.getCell(21, 8)));
		assertTrue(testList.contains(board.getCell(19, 8)));
		assertTrue(testList.contains(board.getCell(20, 5)));
	}

	// Test adjacencies for different walkway cells
	// These are dark orange on spreadsheet
	@Test
	public void testAdjacencyWalkways() {
		// Test a corner walkway with 2 adjacencies
		Set<BoardCell> testList = board.getAdjList(5, 5);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(6, 5)));
		assertTrue(testList.contains(board.getCell(5, 4)));

		// Test a walkway diagnol to a doorway, making sure doorway is not included
		testList = board.getAdjList(13,10);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(12, 10)));
		assertTrue(testList.contains(board.getCell(14, 10)));
		assertTrue(testList.contains(board.getCell(13, 11)));
		assertTrue(testList.contains(board.getCell(13, 9)));

		// Test walkway adjacent to a doorway and two other walkways
		testList = board.getAdjList(23, 26);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(23, 27)));
		assertTrue(testList.contains(board.getCell(23, 25)));
		assertTrue(testList.contains(board.getCell(24, 26)));

		// Test a walkway in a corner with only one adjacency
		testList = board.getAdjList(3, 19);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(3, 18)));
	}

	// Test targets with rolls 1, 3, and 4 for a doorway piece
	// Tests are light blue on spreadsheet
	@Test
	public void TestDoorwayTargets() {
		// Test a roll of 1
		board.calcTargets(board.getCell(22, 10), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(25, 8)));
		assertTrue(targets.contains(board.getCell(22, 11)));
		assertTrue(targets.contains(board.getCell(22, 9)));
		assertTrue(targets.contains(board.getCell(21, 10)));

		// Test a roll of 3
		board.calcTargets(board.getCell(22, 10), 3);
		targets = board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(25, 8)));
		assertTrue(targets.contains(board.getCell(22, 7)));
		assertTrue(targets.contains(board.getCell(22, 13)));
		assertTrue(targets.contains(board.getCell(21, 8)));
		assertTrue(targets.contains(board.getCell(21, 12)));
		assertTrue(targets.contains(board.getCell(20, 9)));
		assertTrue(targets.contains(board.getCell(20, 11)));

		// Test a roll of 4
		board.calcTargets(board.getCell(22, 10), 4);
		targets = board.getTargets();
		assertEquals(17, targets.size());
		assertTrue(targets.contains(board.getCell(25, 8)));
		assertTrue(targets.contains(board.getCell(19, 9)));
		assertTrue(targets.contains(board.getCell(19, 11)));
		assertTrue(targets.contains(board.getCell(21, 13)));
		assertTrue(targets.contains(board.getCell(23, 7)));
		assertTrue(targets.contains(board.getCell(24, 12)));
	}

	// Test a walkway with walls near it with rolls 1, 3, 4
	// Tests are light blue on spreadsheet
	@Test
	public void TestWalkwayTargets() {
		// Test a roll of 1
		board.calcTargets(board.getCell(27, 15), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(27, 16)));
		assertTrue(targets.contains(board.getCell(27, 14)));
		assertTrue(targets.contains(board.getCell(28, 15)));

		// Test a roll of 3
		board.calcTargets(board.getCell(27, 15), 3);
		targets = board.getTargets();
		assertEquals(12, targets.size()); 
		assertTrue(targets.contains(board.getCell(30, 15)));
		assertTrue(targets.contains(board.getCell(27, 12)));
		assertTrue(targets.contains(board.getCell(27, 18)));
		assertTrue(targets.contains(board.getCell(28, 15)));
		assertTrue(targets.contains(board.getCell(25, 14)));

		// Test a roll of 4
		board.calcTargets(board.getCell(27, 15), 4);
		targets = board.getTargets();
		assertEquals(15, targets.size());
		assertTrue(targets.contains(board.getCell(24, 14)));
		assertTrue(targets.contains(board.getCell(26, 18)));
		assertTrue(targets.contains(board.getCell(30, 16)));
		assertTrue(targets.contains(board.getCell(28, 14)));
	}

	// Test a walkway near a doorway
	// Test is light blue in spreadsheet
	@Test
	public void TestWalkwayTargets2() {
		// Test a roll of 1
		board.calcTargets(board.getCell(9, 23), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(8, 23)));
		assertTrue(targets.contains(board.getCell(10, 23)));
		assertTrue(targets.contains(board.getCell(9, 24)));
		assertTrue(targets.contains(board.getCell(9, 22)));

		// Test a roll of 3
		board.calcTargets(board.getCell(9, 23), 3);
		targets = board.getTargets();
		assertEquals(14, targets.size());
		assertTrue(targets.contains(board.getCell(6, 23)));
		assertTrue(targets.contains(board.getCell(9, 20)));
		assertTrue(targets.contains(board.getCell(9, 24)));
		assertTrue(targets.contains(board.getCell(10, 26)));

		// Test a roll of 4
		board.calcTargets(board.getCell(9, 23), 4);
		targets = board.getTargets();
		assertEquals(18, targets.size());
		assertTrue(targets.contains(board.getCell(12, 24)));
		assertTrue(targets.contains(board.getCell(5, 23)));
		assertTrue(targets.contains(board.getCell(8, 22)));
		assertTrue(targets.contains(board.getCell(10, 20)));
		assertTrue(targets.contains(board.getCell(10, 26)));
	}

	// Test a room center with a secret passage with rolls 1, 3, 4
	// Test is light blue in spreadsheet
	@Test
	public void TestRoomCenterTargets() {
		// Test a roll of 1
		board.calcTargets(board.getCell(26, 21), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(24, 22)));
		assertTrue(targets.contains(board.getCell(26, 18)));
		assertTrue(targets.contains(board.getCell(5, 9)));

		// Test a roll of 3
		board.calcTargets(board.getCell(26, 21), 3);
		targets = board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(28, 18)));
		assertTrue(targets.contains(board.getCell(25, 19)));
		assertTrue(targets.contains(board.getCell(24, 24)));
		assertTrue(targets.contains(board.getCell(5, 9)));

		// Test a roll of 4
		board.calcTargets(board.getCell(26, 21), 4);
		targets = board.getTargets();
		assertEquals(18, targets.size());
		assertTrue(targets.contains(board.getCell(22, 23)));
		assertTrue(targets.contains(board.getCell(23, 18)));
		assertTrue(targets.contains(board.getCell(26, 17)));
		assertTrue(targets.contains(board.getCell(5, 9)));
	}
	
	// Test targets to make sure occupied locations don't cause problems
	// Tests are light blue, occupied cells are dark blue in spreadsheet
	@Test
	public void testTargetsOccupied() {
		// Test a roll of 2 with two blocked cells
		board.getCell(27, 17).setOccupied(true);
		board.getCell(26, 14).setOccupied(true);
		board.calcTargets(board.getCell(27, 15), 2);
		board.getCell(27, 17).setOccupied(false);
		board.getCell(26, 14).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(29, 15)));
		assertTrue(targets.contains(board.getCell(28, 14)));
		assertTrue(targets.contains(board.getCell(28, 16)));
		assertTrue(targets.contains(board.getCell(27, 13)));
		assertFalse(targets.contains(board.getCell(27, 17)));
		assertFalse(targets.contains(board.getCell(26, 14)));
		
		// Test and make sure we can get into an occupied room
		board.getCell(26, 21).setOccupied(true);
		board.getCell(27, 17).setOccupied(true);
		board.calcTargets(board.getCell(26, 18), 2);
		board.getCell(26, 21).setOccupied(false);
		board.getCell(27, 17).setOccupied(false);
		targets = board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCell(26,21)));
		assertTrue(targets.contains(board.getCell(24, 18)));
		assertFalse(targets.contains(board.getCell(27, 17)));
		
		// Test leaving a room with a blocked doorway
		board.getCell(9, 24).setOccupied(true);
		board.calcTargets(board.getCell(10, 26), 2);
		board.getCell(9, 24).setOccupied(false);
		targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(13, 25)));
		assertTrue(targets.contains(board.getCell(12, 24)));
	}






}
