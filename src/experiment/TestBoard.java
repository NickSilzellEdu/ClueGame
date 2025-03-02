/*
 * Created by Nick Silzell and Andrwe Grimes on Feb 25 2025
 */

package experiment;
import java.util.Set;
import java.util.HashSet;
public class TestBoard {	
	private TestBoardCell[][] grid;
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;
	final static int COLS = 4;
	final static int ROWS = 4;

	public TestBoard() {
		// Initialize sets
		targets = new HashSet<TestBoardCell>();
		visited = new HashSet<TestBoardCell>();

		// Initialize grid
		grid = new TestBoardCell[ROWS][COLS];
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				grid[row][col] = new TestBoardCell(row,col);
			}
		}

		// Set up adjacency list for each cell
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				if(row > 0) grid[row][col].addAdjacency(grid[row - 1][col]);
				if(row < ROWS - 1) grid[row][col].addAdjacency(grid[row + 1][col]);
				if(col > 0) grid[row][col].addAdjacency(grid[row][col - 1]);
				if(col < COLS - 1) grid[row][col].addAdjacency(grid[row][col + 1]);
			}
		}
	}

	// Calculates legal targets for a move from startCell of length pathLength
	public void calcTargets(TestBoardCell startCell, int pathLength) {
		visited.add(startCell);

		// Visit current cell's adjacent cells
		for(TestBoardCell cell : startCell.getAdjList()) {
			if(!visited.contains(cell)) {
				visited.add(cell);
				// If out of steps and empty add to targets, if not call recursively on adjList
				if(pathLength == 1 || cell.isRoom()) {
					if(!cell.getOccupied()) targets.add(cell);
				} else {
					if(!(cell.isRoom() || cell.getOccupied())) calcTargets(cell, pathLength - 1);
				}
				visited.remove(cell);
			}
		}
	}

	// Returns the cell from the board at row, col
	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	// Gets the targets last created by calcTargest()
	public Set<TestBoardCell> getTargets(){
		return targets;
	}
}
