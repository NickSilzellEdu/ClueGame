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

	// new stuff lines 33-55
	public void initialize() {
	    // Initialize sets FIRST
	    targets = new HashSet<BoardCell>();
	    visited = new HashSet<BoardCell>();

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

	    // Set up adjacency list for each cell
	    calcAdjacencies();
	}


	// Return the only method of Board
	public static Board getInstance() {
		return theInstance;
	}
	
	// new stuff lines 62-82
	public void calcTargets(BoardCell startCell, int pathLength) {
	    targets.clear();  // Important to clear targets before each calculation
	    visited.clear();  // Clear visited set for fresh calculation
	    visited.add(startCell);
	    findAllTargets(startCell, pathLength);
	}

	private void findAllTargets(BoardCell currentCell, int stepsRemaining) {
	    for (BoardCell adjCell : currentCell.getAdjList()) {
	        if (visited.contains(adjCell) || (adjCell.getOccupied() && !adjCell.isRoomCenter())) {
	            continue; // Skip visited cells or occupied cells unless they're room centers
	        }
	        visited.add(adjCell);
	        if (stepsRemaining == 1 || adjCell.isRoomCenter()) {
	            targets.add(adjCell);
	        } else {
	            findAllTargets(adjCell, stepsRemaining - 1);
	        }
	        visited.remove(adjCell);  // Important for backtracking
	    }
	}


	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	// Gets the targets last created by calcTargets()
	public Set<BoardCell> getTargets(){
		return targets;
	}

	// Return the room based on it's symbol
	public Room getRoom(Character roomChar) {
		return roomMap.get(roomChar);
	}

	// Return the room based on it's cell
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
				else if(roomMap != null && roomMap.containsKey(initial)) cell.setIsRoom(true); // Set to a room cell if true

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
							// new stuff lines 234-242
							// SecretPassage indicator
							if (token.length() == 2 && Character.isLetter(ch)) {
							    cell.setSecretPassage(ch);
							    if (cell.isRoomCenter()) {
							        Room currentRoom = roomMap.get(initial);
							        if (currentRoom != null) {
							            currentRoom.setCenterCell(cell);
							        }
							    }
							}
							break;
						}
					}
				}
				grid[r][c] = cell;
			}
		}
	}

	// Get adjacency list for the cell at row, col
	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}

	// Populate adjacency for a cell
	private void calcAdjacencies() {
		// Go through each cell, and check walkways and doorways. 
		BoardCell cell = null;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
//				
//				//test cell - remove after tests
//				int row = 19;
//				int col = 5;
				
				cell = grid[row][col];
				


				// If the cell is a walkway
				if(cell.getInitial() == 'W' || cell.isDoorway()) {
					if (isValidIndex(row - 1, col) && isValidCell(grid[row-1][col])) {
						cell.addAdjacency(grid[row - 1][col]);
					}
					if (isValidIndex(row + 1, col) && isValidCell(grid[row + 1][col])) {
						cell.addAdjacency(grid[row + 1][col]);
					}
					if (isValidIndex(row, col - 1) && isValidCell(grid[row][col - 1])) {
						cell.addAdjacency(grid[row][col - 1]);
					}
					if (isValidIndex(row, col + 1) && isValidCell(grid[row][col + 1])) {
						cell.addAdjacency(grid[row][col + 1]);
					}
				}

				// If cell is a doorway, add the room center, and also add this cell to room center's adjacencies
				if(cell.isDoorway()) {
					BoardCell center = findCenterCell(cell);
					if(center != null) {
					center.addAdjacency(cell);
					cell.addAdjacency(center);
					}
				}

				// Add adjacencies to both room centers
				if(cell.isRoom() && cell.getSecretPassage() != '\0') {
					Room adjRoom = getRoom(cell.getSecretPassage());
					BoardCell roomCenter = roomMap.get(cell.getInitial()).getCenterCell();
					if(adjRoom != null && adjRoom.getCenterCell() != null) {
						BoardCell adjCenter = adjRoom.getCenterCell();
						roomCenter.addAdjacency(adjCenter);
						adjCenter.addAdjacency(roomCenter);
					}

				}
			}
		}
		for (Room room : roomMap.values()) {
		    BoardCell centerCell = room.getCenterCell();
		    if (centerCell != null && centerCell.getSecretPassage() != '\0') {
		        Room passageRoom = roomMap.get(centerCell.getSecretPassage());
		        if (passageRoom != null && passageRoom.getCenterCell() != null) {
		            centerCell.addAdjacency(passageRoom.getCenterCell());
		        }
		    }
		}
	}

	// Make sure row and column are in bounds
	private boolean isValidIndex(int row, int col){
		return (row >= 0 && row < rows && col >= 0 && col < cols);
	}

	// Make sure cell is a walkway or doorway
	private boolean isValidCell(BoardCell cell) {
		return (cell.getInitial() == 'W' || cell.isDoorway());
	}
	// Find room center for door cell based on direction
	private BoardCell findCenterCell(BoardCell doorCell) {
		int row = doorCell.getRow();
		int col = doorCell.getCol();
		DoorDirection direction = doorCell.getDoorDirection();

		// Use the character of the adjacent room cell to locate room center
		switch(direction) {
		case UP:
			if(isValidIndex(row - 1, col)) {
				char roomChar = grid[row - 1][col].getInitial();
				return roomMap.get(roomChar).getCenterCell();
			}
			break;
		case DOWN:
			if(isValidIndex(row + 1, col)) {
				char roomChar = grid[row + 1][col].getInitial();
				return roomMap.get(roomChar).getCenterCell();
			}
			break;
		case LEFT:
			if(isValidIndex(row, col - 1)) {
				char roomChar = grid[row][col - 1].getInitial();
				return roomMap.get(roomChar).getCenterCell();
			}
			break;
		case RIGHT:
			if(isValidIndex(row, col + 1)) {
				char roomChar = grid[row][col + 1].getInitial();
				return roomMap.get(roomChar).getCenterCell();
			}
			break;
		default:
			break;	
		}
		// If no valid cells, return null
		return null;
	}
}
