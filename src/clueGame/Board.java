package clueGame;

/*
 * Created by Nick Silzell and Andrwe Grimes on Mar 2 2025
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
		roomMap = new HashMap<Character, Room>(); // Initialize even if initialize() is not called
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

		// Initialize sets
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();

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
		// Set up config file
		try(Scanner scan = new Scanner(new FileReader(this.setupConfigFile))) {

			// Read setup file line by line, and add to roomMap if valid
			// For now: spaces are considered rooms
			String currentLine = "";
			while(scan.hasNextLine()) {
				currentLine = scan.nextLine();
				String [] splitLine = currentLine.split("[,]"); // Split on comma + space

				if(splitLine[0].trim().equals("Room") || splitLine[0].trim().equals("Space")) { // Add all rooms to the map
					String roomName = splitLine[1].trim();
					Character roomChar = splitLine[2].trim().charAt(0);

					Room roomToAdd = new Room(roomName);
					roomMap.put(roomChar, roomToAdd);
				} else if(!splitLine[0].startsWith("//")) throw new BadConfigFormatException("Error: \"" + this.setupConfigFile + "\" is not properly configured for setup");
			}
		}
	}

	// Use scanner to read in grid layout
	public void loadLayoutConfig() throws FileNotFoundException, BadConfigFormatException {
		// read nonempty lines from layout file
		List<String> lines = new ArrayList<String>();
		try (Scanner scanner = new Scanner(new FileReader(layoutConfigFile))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					lines.add(line);
				}
			}

		} catch (Exception ex) {
			throw new BadConfigFormatException("cant read layout file: " + ex.getMessage());
		}

		if (lines.isEmpty()) {
			throw new BadConfigFormatException("empty layout file");
		}

		// amt of rows and cols from the file
		int numRows = lines.size();
		String[] firstTokens = lines.get(0).split(",");
		int numCols = firstTokens.length;

		// validate amt of rows matches amt of cols
		for (int r = 0; r < numRows; r++) {
			String[] tokens = lines.get(r).split(",");
			if (tokens.length != numCols) {
				throw new BadConfigFormatException("row " + r + " has " + tokens.length + " columns, expected " + numCols);
			}
		}

		// set board dimensions
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

				// create new cell
				BoardCell cell = new BoardCell(r, c);
				char initial = token.charAt(0);
				cell.setInitial(initial);

				// verify room initial exists in roomMap
				if (roomMap != null && !roomMap.containsKey(initial)) {
					throw new BadConfigFormatException("unknown room initial '" + initial + "' at row " + r + ", column " + c);
				}

				// process extra character
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
							// secretPassage indicator
							if (token.length() == 2) {
								cell.setSecretPassage(ch);
							}
							break;
						}
					}
				}
				grid[r][c] = cell;
			}
		}
	}

}
