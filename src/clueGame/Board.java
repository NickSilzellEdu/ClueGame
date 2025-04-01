package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes on Mar 2 2025
 * This class represents the Clue Game board, a singleton class
 */

import java.util.Set;
import java.util.Collections;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.Color;
import java.util.Random;

public class Board {
	public static final int NUM_PLAYERS = 6;
	public static final int NUM_ROOMS = 9;
	public static final int NUM_WEAPONS = 6;
	public static final int DECK_SIZE = NUM_PLAYERS + NUM_ROOMS + NUM_WEAPONS;
	private static Board theInstance = new Board();
	private BoardCell[][] grid;
	private String layoutConfigFile;
	private String setupConfigFile;
	private Map<Character, Room> roomMap;
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;
	private int rows;
	private int cols;
	private ArrayList<Card> deck;
	private Solution theAnswer;
	private ArrayList<Player> players;


	private Board() {
		super();
	}

	public void initialize() {
		// Initialize variables
		this.players = new ArrayList<Player>(); 
		this.deck = new ArrayList<Card>(); 

		// Initialize sets for targets algorithm
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

		// Get solution from deck
		getRandomSolution();
		
		// Deal the rest of trds 
		deal();

	}

	// Return the only instance of Board
	public static Board getInstance() {
		return theInstance;
	}

	// Setup properly for calcTargetsRecursive
	public void calcTargets(BoardCell startCell, int pathLength) {
		targets.clear();  // Important to clear targets before each calculation
		visited.clear();  // Clear visited set for fresh calculation
		visited.add(startCell);
		calcTargetsRecursive(startCell, pathLength); // Recursively add proper cells to targets
	}

	// Recursively add proper targets to targets set
	private void calcTargetsRecursive(BoardCell currentCell, int stepsRemaining) {
		// Iterate through each adjacency
		for (BoardCell adjCell : currentCell.getAdjList()) {
			if (visited.contains(adjCell) || (adjCell.getOccupied() && !adjCell.isRoomCenter())) {
				continue; // Skip visited cells or occupied cells unless they're room centers
			}
			visited.add(adjCell);
			// If we have one more step, this cell is a target so add it unless it is a room center
			if (stepsRemaining == 1 || adjCell.isRoomCenter()) {
				targets.add(adjCell);
			} else {
				calcTargetsRecursive(adjCell, stepsRemaining - 1);
			}
			visited.remove(adjCell); // Make sure to only go through each cell once
		}
	}

	// Use scanner to read in room setups from setup file
	public void loadSetupConfig() throws FileNotFoundException, BadConfigFormatException{
		int playersLoaded = 0; // For determining computer player vs human player

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

				// Handle spaces that require room initials
				if(splitLine[0].trim().equals("Room") || splitLine[0].trim().equals("Space")) { // Add all rooms and spaces to roomMap
					String roomName = splitLine[1].trim();
					Character roomChar = splitLine[2].trim().charAt(0);
					Room roomToAdd = new Room(roomName);
					roomMap.put(roomChar, roomToAdd);

					// If card is a room, add it to deck
					if(splitLine[0].trim().equals("Room")) deck.add(new Card(roomName, CardType.ROOM));
				}
				// If card is a weapon, add it to deck
				else if(splitLine[0].trim().equals("Weapon")) deck.add(new Card(splitLine[1].trim(), CardType.WEAPON));
				// If card is a player add it to deck

				else if(splitLine[0].trim().equals("Player")) {
					// Make sure Color read in is valid
					Color playerColor = getColorFromName(splitLine[2].trim()); // make sure color is valid
					if(playerColor == null) throw new BadConfigFormatException("Error: invalid color");	
					// Load the first player as a human player
					if(playersLoaded == 0) players.add(new HumanPlayer(splitLine[1].trim(), playerColor, Integer.parseInt(splitLine[3].trim()), Integer.parseInt(splitLine[4].trim())));
					else players.add(new ComputerPlayer(splitLine[1].trim(), playerColor, Integer.parseInt(splitLine[3].trim()), Integer.parseInt(splitLine[4].trim())));
					deck.add(new Card(splitLine[1].trim(), CardType.PERSON));
					playersLoaded++;
				}
				// If line does not start with //, Room, or Space, throw an error
				else if(!splitLine[0].startsWith("//")) throw new BadConfigFormatException("Error: \"" + this.setupConfigFile + "\" is not properly configured for setup");
			}
		}
		// Possible if any data is missing and splitLine[i] fails
		catch(NullPointerException e) {
			throw new BadConfigFormatException("Error: \"" + this.setupConfigFile + "\" is not properly configured for setup");		}
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

		// Iterate through each row and column and setup the cell and it's initials
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
						// Doors
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
						case '*': // Room Center
							cell.setRoomCenter(true);
							roomMap.get(initial).setCenterCell(cell);
							break;
						case '#': // Room label
							cell.setLabel(true);
							roomMap.get(initial).setLabelCell(cell);
							break;

						default:
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
				grid[r][c] = cell; // Update grid to have this cell
			}
		}
	}

	// Populate adjacency for a cell
	private void calcAdjacencies() {
		// Go through each cell, and check walkways and doorways. 
		BoardCell cell = null;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {

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
					// If a secret room exists, and it has a center cell, make them adjacent
					if(adjRoom != null && adjRoom.getCenterCell() != null) {
						BoardCell adjCenter = adjRoom.getCenterCell();
						roomCenter.addAdjacency(adjCenter);
						adjCenter.addAdjacency(roomCenter);
					}
				}
			}
		}
		// Loop through each room to connect its center cell to the center cell of the secret room
		for (Room room : roomMap.values()) {
			// Get the room's center cell and check if it has a secret passage
			BoardCell centerCell = room.getCenterCell();
			if (centerCell != null && centerCell.getSecretPassage() != '\0') {
				Room passageRoom = roomMap.get(centerCell.getSecretPassage());
				// If the destination room and its center cell exist, link them as adjacent cells
				if (passageRoom != null && passageRoom.getCenterCell() != null) {
					centerCell.addAdjacency(passageRoom.getCenterCell());
				}
			}
		}
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

	// Helper function to read config file, 
	public static Color getColorFromName(String name) {
		String lowerCaseName = name.toLowerCase();
		switch (lowerCaseName.toLowerCase()) {
		case "black": return Color.BLACK;
		case "blue": return Color.BLUE;
		case "cyan": return Color.CYAN;
		case "dark_gray": return Color.DARK_GRAY;
		case "gray": return Color.GRAY;
		case "green": return Color.GREEN;
		case "light_gray": return Color.LIGHT_GRAY;
		case "magenta": return Color.MAGENTA;
		case "orange": return Color.ORANGE;
		case "pink": return Color.PINK;
		case "red": return Color.RED;
		case "white": return Color.WHITE;
		case "yellow": return Color.YELLOW;
		default: return null; 
		}
	}

	// Helper function to get a random solution from the deck
	// Public for testing purposes
	public void getRandomSolution() {
		Random rand = new Random();

		// Make sure deck is in order Room, Person, Weapon
		Collections.sort(deck, Comparator.comparing(Card::getType));
		// Sort into type lists
		List<Card> roomCards = deck.subList(0, NUM_ROOMS);
		List<Card> playerCards = deck.subList(NUM_ROOMS, NUM_ROOMS + NUM_PLAYERS);
		List<Card> weaponCards = deck.subList(NUM_ROOMS + NUM_PLAYERS, deck.size());


		// Make sure it is not already initialized, get random solution
		if(theAnswer == null) theAnswer = new Solution(null, null, null);
		theAnswer = new Solution(roomCards.get(rand.nextInt(roomCards.size())), playerCards.get(rand.nextInt(playerCards.size())), weaponCards.get(rand.nextInt((weaponCards.size()))));
		
	}
	// Deal cards to players
	public void deal() {

		ArrayList<Card> cardsToDeal = new ArrayList<Card>(deck); // Create a copy so we don't affect the actual deck
		// Remove the solution cards
		cardsToDeal.remove(theAnswer.getRoom());
		cardsToDeal.remove(theAnswer.getPerson());
		cardsToDeal.remove(theAnswer.getWeapon());
		int playerIndex = 0; // keep track of which player gets the card using modulus
		Random rand = new Random(); // for random card picking
		
		// While the deck has cards, deal one random card to each player
		while(!cardsToDeal.isEmpty()) {
			// get a random number, and add it to a player's hand
			Card cardDealt = cardsToDeal.get(rand.nextInt(cardsToDeal.size()));
			players.get(playerIndex % NUM_PLAYERS).getHand().add(cardDealt);
			cardsToDeal.remove(cardDealt);
			playerIndex++;
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

	// Get adjacency list for the cell at row, col
	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}

	// Return players
	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	// Return the Solution
	public Solution getSolution() {
		return this.theAnswer;
	}

	// Return deck
	public ArrayList<Card> getDeck(){
		return this.deck;
	}

}
