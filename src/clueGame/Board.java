package clueGame;

/*
 * Created by Nick Silzell and Andrwe Grimes on Mar 2 2025
 */
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
public class Board {
	private static Board theInstance = new Board();
	private BoardCell[][] grid;
	private String layoutConfigFile;
	private String setupConfigFile;
	private Map<Character, Room> roomMap;
	
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;
	final static int COLS = 4;
	final static int ROWS = 4;
	
	private Board() {
		super();
		// Initialize sets
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();
		
		// Initialize grid
		grid = new BoardCell[ROWS][COLS];
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				grid[row][col] = new BoardCell(row,col);
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
	
	// Return the only method of Board
	public static Board getInstance() {
		return theInstance;
	}
	
	// Calculates legal targets for a move from startCell of length pathLength
	public void calcTargets(BoardCell startCell, int pathLength) {
		visited.add(startCell);
		
		// Visit current cell's adjacent cells
		for(BoardCell cell : startCell.getAdjList()) {
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
	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}
	
	// Gets the targets last created by calcTargest()
	public Set<BoardCell> getTargets(){
		return targets;
	}
	
	// Set 
}
