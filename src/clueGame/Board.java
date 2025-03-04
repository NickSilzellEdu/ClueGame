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
	private int rows;
	private int cols;

	private Board() {
		super();
	}

	// Initialize board
	public void initialize() {
		
		//INCOMPLETE: get rows and cols from csv
		this.rows = 30;
		this.cols = 30;
		
		
		// Initialize sets
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();

		// Initialize grid
		grid = new BoardCell[rows][cols];
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				grid[row][col] = new BoardCell(row,col);
			}
		}

		// Set up adjacency list for each cell
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				if(row > 0) grid[row][col].addAdjacency(grid[row - 1][col]);
				if(row < rows - 1) grid[row][col].addAdjacency(grid[row + 1][col]);
				if(col > 0) grid[row][col].addAdjacency(grid[row][col - 1]);
				if(col < cols - 1) grid[row][col].addAdjacency(grid[row][col + 1]);
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

	// Return the room based on it's name
	public Room getRoom(Character roomChar) {
		// INCOMPLETE
		return new Room("Fail");
	}
	
	// Return the room based on it's cell
	public Room getRoom(BoardCell cell) {
		//INCOMPLETE
		return new Room("Fail");
	}
	
	public int getNumRows() {
		return rows;
	}
	
	public int getNumColumns() {
		return cols;
	}
	
	public void setConfigFiles(String layout, String setup) {
		this.layoutConfigFile = layout;
		this.setupConfigFile = setup;
	}
	public void loadSetupConfig(){
		//INCOMPLETE
	}
	
	public void loadLayoutConfig() {
		//INCOMPLETE
	}
}
