package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes on Mar 2 2025
 * This class represents the Clue Game board
 */
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

	// Initialize board, load both config files
	public void initialize() {
		// Load layout and setup files
		try {
			this.loadSetupConfig();
			this.loadLayoutConfig();
		}
		catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
			return; // Exit 
		}
		catch(BadConfigFormatException e) {
			System.out.println(e.getMessage());
			return; // Exit
		}

		// ORIGINAL: Set up adjacency list for each cell using simple up/down/left/right connections
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				if(row > 0) grid[row][col].addAdjacency(grid[row - 1][col]);
				if(row < rows - 1) grid[row][col].addAdjacency(grid[row + 1][col]);
				if(col > 0) grid[row][col].addAdjacency(grid[row][col - 1]);
				if(col < cols - 1) grid[row][col].addAdjacency(grid[row][col + 1]);
			}
		}

		// NEW: Recalculate adjacencies using advanced rules (doors, secret passages, room centers)
		// This method will clear the previously set simple adjacencies and add new ones.
		calcAdvancedAdjacencies();

		// Initialize sets for targets and visited cells (used in movement calculation)
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();
	}

	// Return the only instance of Board
	public static Board getInstance() {
		return theInstance;
	}

	// Calculates legal targets for a move from startCell of length pathLength
	public void calcTargets(BoardCell startCell, int pathLength) {
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}

	private void findAllTargets(BoardCell thisCell, int steps) {
		for(BoardCell adj : thisCell.getAdjList()) {
			if(!visited.contains(adj)) {
				// NEW: If occupant is blocking, skip this adjacent cell.
				if(adj.getOccupied()) {
					continue;
				}
				visited.add(adj);
				// NEW: If we are out of steps or the cell is a room center, add to targets.
				if(steps == 1 || adj.isRoomCenter()) {
					targets.add(adj);
				} else {
					findAllTargets(adj, steps - 1);
				}
				visited.remove(adj);
			}
		}
	}

	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	// Gets the targets last created by calcTargets()
	public Set<BoardCell> getTargets(){
		return targets;
	}

	// Return the room based on its symbol
	public Room getRoom(Character roomChar) {
		return roomMap.get(roomChar);
	}

	// Return the room based on its cell
	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
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

	// Use scanner to read in room setups from setup file
	public void loadSetupConfig() throws FileNotFoundException, BadConfigFormatException{
		// Make sure roomMap gets initialized during testing:
		if(roomMap == null) roomMap = new HashMap<Character, Room>();
		
		// Set up config file
		try(Scanner scan = new Scanner(new FileReader(this.setupConfigFile))) {
			// Read setup file line by line, and add to roomMap if valid
			// For now: spaces are considered rooms
			String currentLine = "";
			while(scan.hasNextLine()) {
				currentLine = scan.nextLine();
				String [] splitLine = currentLine.split("[,]"); // Split on commas

				if(splitLine[0].trim().equals("Room") || splitLine[0].trim().equals("Space")) { // Add all rooms and spaces to roomMap
					String roomName = splitLine[1].trim();
					Character roomChar = splitLine[2].trim().charAt(0);
					Room roomToAdd = new Room(roomName);
					roomMap.put(roomChar, roomToAdd);
				}
				// If line does not start with //, Room, or Space, throw an error
				else if(!splitLine[0].startsWith("//")) throw new BadConfigFormatException("Error: \"" + this.setupConfigFile + "\" is not properly configured for setup");
			}
		}
	}

	// Use scanner to read in grid layout
	public void loadLayoutConfig() throws FileNotFoundException, BadConfigFormatException {		
		// Make sure roomMap gets initialized during testing:
		if(roomMap == null) roomMap = new HashMap<Character, Room>();
		
		// Read nonempty lines from layout file
		List<String> lines = new ArrayList<String>();
		try (Scanner scanner = new Scanner(new FileReader(layoutConfigFile))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					lines.add(line);
				}
			}

		} catch (Exception ex) {
			throw new BadConfigFormatException("Error: cannot read layout file, " + ex.getMessage());
		}

		if (lines.isEmpty()) {
			throw new BadConfigFormatException("Error: Layout file is empty");
		}

		// Amount of rows and columns from the file
		int numRows = lines.size();
		String[] firstTokens = lines.get(0).split(",");
		int numCols = firstTokens.length;

		// Validate amount of rows matches amount of columns
		for (int r = 0; r < numRows; r++) {
			String[] tokens = lines.get(r).split(",");
			if (tokens.length != numCols) {
				throw new BadConfigFormatException("row " + r + " has " + tokens.length + " columns, expected " + numCols);
			}
		}

		// Set board dimensions
		this.rows = numRows;
		this.cols = numCols;
		grid = new BoardCell[rows][cols];

		for (int r = 0; r < rows; r++) {
			String[] tokens = lines.get(r).split(",");
			for (int c = 0; c < cols; c++) {
				String token = tokens[c].trim();
				if (token.isEmpty()) {
					throw new BadConfigFormatException("empty token at row " + r + ", column " + c);
				}

				// Create new cell
				BoardCell cell = new BoardCell(r, c);
				char initial = token.charAt(0);
				cell.setInitial(initial);

				// Verify room initial exists in roomMap
				if (roomMap != null && !roomMap.containsKey(initial)) {
					throw new BadConfigFormatException("unknown room initial '" + initial + "' at row " + r + ", column " + c);
				}

				// Process extra character if exists
				if (token.length() > 1) {
					for (int i = 1; i < token.length(); i++) {
						char ch = token.charAt(i);
						switch (ch) {
						case '<':
							cell.setDoorDirection(DoorDirection.LEFT);
							break;
						case '^':
							cell.setDoorDirection(DoorDirection.UP);
							break;
						case '>':
							cell.setDoorDirection(DoorDirection.RIGHT);
							break;
						case 'v':
							cell.setDoorDirection(DoorDirection.DOWN);
							break;
						case '*':
							cell.setRoomCenter(true);
							roomMap.get(initial).setCenterCell(cell);
							break;
						case '#':
							cell.setLabel(true);
							roomMap.get(initial).setLabelCell(cell);
							break;

						default:
							// SecretPassage indicator
							if (token.length() == 2) {
								cell.setSecretPassage(ch);
							}
							break;
						}
					}
				}
				// Determine if cell is a room cell (non-walkway and non-unused)
				if (initial != 'W' && initial != 'X') {
					cell.setIsRoom(true);
				}
				grid[r][c] = cell;
			}
		}
	}
	
	// NEW: Advanced adjacency calculation method.
	// This method clears any previously set adjacencies and then sets them up based on:
	// - Walkways only connect to adjacent walkways or doorways (if entry is valid)
	// - Doorways connect to adjacent walkways and to their corresponding room center
	// - Room center cells connect to doorways and to a secret passage partner, if any.
	private void calcAdvancedAdjacencies() {
		// Clear all adjacencies first
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				grid[r][c].getAdjList().clear();
			}
		}
		
		// First pass: For each cell that is a walkway or a doorway, check the four directions.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				BoardCell cell = grid[r][c];
				
				// Only process if cell is a walkway ('W') or a doorway
				if (cell.getInitial() == 'W' || cell.isDoorway()) {
					if (validIndex(r - 1, c)) {
						addIfValid(cell, grid[r - 1][c]);
					}
					if (validIndex(r + 1, c)) {
						addIfValid(cell, grid[r + 1][c]);
					}
					if (validIndex(r, c - 1)) {
						addIfValid(cell, grid[r][c - 1]);
					}
					if (validIndex(r, c + 1)) {
						addIfValid(cell, grid[r][c + 1]);
					}
				}
				
				// For room centers with secret passages, link to the corresponding room center.
				if (cell.isRoomCenter() && cell.getSecretPassage() != '\0') {
					Room passageRoom = getRoom(cell.getSecretPassage());
					if (passageRoom != null && passageRoom.getCenterCell() != null) {
						BoardCell otherCenter = passageRoom.getCenterCell();
						cell.addAdjacency(otherCenter);
						otherCenter.addAdjacency(cell);
					}
				}
			}
		}
		
		// Second pass: For each doorway, link it to its room center.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				BoardCell cell = grid[r][c];
				if (cell.isDoorway()) {
					BoardCell center = findCenterCellForDoor(cell);
					if (center != null) {
						cell.addAdjacency(center);
						center.addAdjacency(cell);
					}
				}
			}
		}
	}
	
	// NEW: Helper method for advanced adjacency.
	// Adds neighbor to a cell's adjacency list if valid.
	private void addIfValid(BoardCell cell, BoardCell neighbor) {
		// For walkways, add neighbor if:
		// - The neighbor is a walkway (and not a doorway), or
		// - The neighbor is a doorway that can be entered from this cell.
		if (cell.getInitial() == 'W') {
			if (neighbor.getInitial() == 'W' && !neighbor.isDoorway()) {
				cell.addAdjacency(neighbor);
			} else if (neighbor.isDoorway()) {
				if (canEnterDoorFrom(cell, neighbor)) {
					cell.addAdjacency(neighbor);
				}
			}
		}
		// For doorways, add adjacent walkways.
		else if (cell.isDoorway() && neighbor.getInitial() == 'W') {
			cell.addAdjacency(neighbor);
		}
	}
	
	// NEW: Determines if a walkway cell can enter a door cell based on door direction.
	private boolean canEnterDoorFrom(BoardCell walkway, BoardCell door) {
		int wr = walkway.getRow();
		int wc = walkway.getCol();
		int dr = door.getRow();
		int dc = door.getCol();
		DoorDirection dir = door.getDoorDirection();
		
		switch (dir) {
			case UP:
				return (dr - 1 == wr && dc == wc);
			case DOWN:
				return (dr + 1 == wr && dc == wc);
			case LEFT:
				return (dc - 1 == wc && dr == wr);
			case RIGHT:
				return (dc + 1 == wc && dr == wr);
			default:
				return false;
		}
	}
	
	// NEW: Finds the room center cell corresponding to a given door cell based on door direction.
	private BoardCell findCenterCellForDoor(BoardCell door) {
		int row = door.getRow();
		int col = door.getCol();
		DoorDirection dir = door.getDoorDirection();
		
		switch (dir) {
			case UP:
				if (validIndex(row - 1, col)) {
					char roomInitial = grid[row - 1][col].getInitial();
					return roomMap.get(roomInitial).getCenterCell();
				}
				break;
			case DOWN:
				if (validIndex(row + 1, col)) {
					char roomInitial = grid[row + 1][col].getInitial();
					return roomMap.get(roomInitial).getCenterCell();
				}
				break;
			case LEFT:
				if (validIndex(row, col - 1)) {
					char roomInitial = grid[row][col - 1].getInitial();
					return roomMap.get(roomInitial).getCenterCell();
				}
				break;
			case RIGHT:
				if (validIndex(row, col + 1)) {
					char roomInitial = grid[row][col + 1].getInitial();
					return roomMap.get(roomInitial).getCenterCell();
				}
				break;
			default:
				break;
		}
		return null;
	}
	
	// NEW: Validates if the given row and column are within board boundaries.
	private boolean validIndex(int r, int c) {
		return (r >= 0 && r < rows && c >= 0 && c < cols);
	}
	
	// NEW: Public helper method to retrieve the adjacency list for the cell at (row, col)
	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}
}
