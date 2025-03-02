/*
 * Created by Nick Silzell and Andrew Grimes Feb 25 2025
 */
package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;


import experiment.*;

public class BoardTestsExp {
	TestBoard board;
	
	@BeforeEach // Run before each test
	public void setUp() {
		// Board should create adjacency list
		board = new TestBoard();
	}
	
	/*
	 * Test adjacencies for several different locations
	 * Test centers and edges
	 */
	@Test
	public void testNWCorner() {
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assertions.assertTrue(testList.contains(board.getCell(1, 0)));
		Assertions.assertTrue(testList.contains(board.getCell(0, 1)));
		Assertions.assertEquals(2, testList.size());
	}
	
	@Test
	public void testSECorner() {
		TestBoardCell cell = board.getCell(3, 3);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assertions.assertTrue(testList.contains(board.getCell(3, 2)));
		Assertions.assertTrue(testList.contains(board.getCell(2, 3)));
		Assertions.assertEquals(2, testList.size());
	}
	
	@Test
	public void testRightEdge() {
		TestBoardCell cell = board.getCell(3, 1);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assertions.assertTrue(testList.contains(board.getCell(3, 0)));
		Assertions.assertTrue(testList.contains(board.getCell(3, 2)));
		Assertions.assertTrue(testList.contains(board.getCell(2, 1)));
		Assertions.assertEquals(3,  testList.size());
	}
	
	@Test
	public void testLeftEdge() {
		TestBoardCell cell = board.getCell(0, 2);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assertions.assertTrue(testList.contains(board.getCell(0, 3)));
		Assertions.assertTrue(testList.contains(board.getCell(1, 2)));
		Assertions.assertTrue(testList.contains(board.getCell(0, 1)));
		Assertions.assertEquals(3, testList.size());
	}
	
	@Test
	public void testMiddle() {
		TestBoardCell cell = board.getCell(2, 2); 
		Set<TestBoardCell> testList = cell.getAdjList();
		Assertions.assertTrue(testList.contains(board.getCell(2, 1)));
		Assertions.assertTrue(testList.contains(board.getCell(2, 3)));
		Assertions.assertTrue(testList.contains(board.getCell(3, 2)));
		Assertions.assertTrue(testList.contains(board.getCell(1, 2)));
		Assertions.assertEquals(4, testList.size());
	}
	
	
	
	/*
	 * Test the calcTargets function with an empty board, occupied spaces, cells that are rooms, and both
	 */
	
	
	/*
	 * Test target creation on a 4x4 board
	 */
	@Test
	public void emptyBoard() {
		TestBoardCell startCell = board.getCell(1, 1);
		board.calcTargets(startCell, 2);
		Set<TestBoardCell> targets = board.getTargets();
		Set<TestBoardCell> expected = new HashSet<>();
		expected.add(board.getCell(0, 0));
		expected.add(board.getCell(2, 0));
		expected.add(board.getCell(0, 2));
		expected.add(board.getCell(1, 3));
		expected.add(board.getCell(2, 2));
		expected.add(board.getCell(3, 1));
		Assertions.assertEquals(expected, targets);
	}
	
	@Test
	public void testOccupiedCellMovement() {
		TestBoardCell startCell = board.getCell(0, 0);
		TestBoardCell occupiedCell = board.getCell(0, 1);
		occupiedCell.setOccupied(true);
		board.calcTargets(startCell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Set<TestBoardCell> expected = new HashSet<>();
		expected.add(board.getCell(1, 2));
		expected.add(board.getCell(2, 1));
		expected.add(board.getCell(3, 0));
		Assertions.assertEquals(expected, targets);
	}
	
	@Test
	public void testRoomStopMovement() {
		TestBoardCell startCell = board.getCell(1, 1);
		TestBoardCell roomCell = board.getCell(1, 2);
		roomCell.setIsRoom(true);
		board.calcTargets(startCell, 2);
		Set<TestBoardCell> targets = board.getTargets();
		Set<TestBoardCell> expected = new HashSet<>();
		expected.add(board.getCell(1, 2));
		expected.add(board.getCell(0, 0));
		expected.add(board.getCell(2, 0));
		expected.add(board.getCell(0, 2));
		expected.add(board.getCell(2, 2));
		expected.add(board.getCell(3, 1));
		Assertions.assertEquals(expected, targets);
	}
	
	/*
	 * Test a corner cell with two targets occupied and dice roll 2
	 */
	@Test
	public void testOccupiedCorner() {
		TestBoardCell cell = board.getCell(3, 3);
		board.getCell(3,1).setOccupied(true);
		board.getCell(1,3).setOccupied(true);
		board.calcTargets(cell, 2);
		Set<TestBoardCell> targets = board.getTargets();
		Assertions.assertEquals(1, targets.size());
		Assertions.assertTrue(targets.contains(board.getCell(2, 2)));
	}
	
	/*
	 * Test targets with several rolls and start locations
	 */
	@Test
	public void testTargetsNormal() {
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Assertions.assertEquals(6, targets.size());
		Assertions.assertTrue(targets.contains(board.getCell(3, 0)));
		Assertions.assertTrue(targets.contains(board.getCell(2, 1)));
		Assertions.assertTrue(targets.contains(board.getCell(0, 1)));
		Assertions.assertTrue(targets.contains(board.getCell(1, 2)));
		Assertions.assertTrue(targets.contains(board.getCell(0, 3)));
		Assertions.assertTrue(targets.contains(board.getCell(1, 0)));
	}
	
	// Test targets with occupied cells
	@Test
	public void testTargetsMixed() {
		// Set up occupied cells
		board.getCell(0, 2).setOccupied(true);
		board.getCell(1, 2).setIsRoom(true);
		TestBoardCell cell = board.getCell(0, 3);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Assertions.assertEquals(3, targets.size());
		Assertions.assertTrue(targets.contains(board.getCell(1, 2)));
		Assertions.assertTrue(targets.contains(board.getCell(2, 2)));
		Assertions.assertTrue(targets.contains(board.getCell(3, 3)));
	}
	
	// Test max roll(6) on an empty board
	@Test
	public void testMaxRoll() {
		TestBoardCell cell = board.getCell(3, 2);
		board.calcTargets(cell, 6);
		Set<TestBoardCell> targets = board.getTargets();
		Assertions.assertEquals(7, targets.size());
		Assertions.assertTrue(targets.contains(board.getCell(0, 1)));
		Assertions.assertTrue(targets.contains(board.getCell(1, 0)));
		Assertions.assertTrue(targets.contains(board.getCell(2, 1)));
		Assertions.assertTrue(targets.contains(board.getCell(1, 2)));
		Assertions.assertTrue(targets.contains(board.getCell(0, 3)));
		Assertions.assertTrue(targets.contains(board.getCell(3, 0)));
		Assertions.assertTrue(targets.contains(board.getCell(2, 3)));
	}
}
