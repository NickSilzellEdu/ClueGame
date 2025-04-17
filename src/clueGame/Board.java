package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes on Mar 2 2025
 * This class represents the Clue Game board, a singleton class
 */

import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.util.Collections;
import java.util.Comparator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.Color;
import java.util.Random;

public class Board {
	private int numPlayers;
	private int numRooms;
	private int numWeapons;
	private int deckSize;
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
	private int currentPlayerIndex;
	private Player currentPlayer;
	private ClueGame gameFrame;
	private GameControlPanel controlPanel;
	private BoardPanel boardPanel;
	private KnownCardsPanel cardsPanel;
	private boolean isHumanTurn;


	private Board() {
		super();
	}

	public void initialize() {
		// Initialize variables
		this.players = new ArrayList<Player>(); 
		this.deck = new ArrayList<Card>(); 
		numPlayers = 0;
		numWeapons = 0;
		numRooms = 0;
		deckSize = 0;
		currentPlayerIndex = 0;

		gameFrame = null;
		controlPanel = null;
		boardPanel = null;
		cardsPanel = null;

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

		// Deal the rest of cards
		deal();

		// Get turns correct for first human player
		if (!players.isEmpty()) currentPlayer = players.get(0);
		isHumanTurn = true;

		// occupy cells of all starting players, and initialize pixel coordinates
		for(Player player : players) {
			getCell(player.getRow(), player.getCol()).setOccupied(true);
		}
		
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
					if(splitLine[0].trim().equals("Room")) {
						deck.add(new Card(roomName, CardType.ROOM));
						numRooms++;
					}
				}
				// If card is a weapon, add it to deck
				else if(splitLine[0].trim().equals("Weapon")) {
					deck.add(new Card(splitLine[1].trim(), CardType.WEAPON));
					numWeapons++;
				}
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
					numPlayers++;
				}
				// If line does not start with //, Room, or Space, throw an error
				else if(!splitLine[0].startsWith("//")) throw new BadConfigFormatException("Error: \"" + this.setupConfigFile + "\" is not properly configured for setup");
			}

			// Update deck size based on the three types of cards
			deckSize = numPlayers + numRooms + numWeapons;

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
				else if(roomMap != null && roomMap.containsKey(initial)) {
					cell.setIsRoom(true); // Set to a room cell if true
					roomMap.get(initial).getRoomCells().add(cell);
				}

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
		// Make sure card setup was correct
		if(numRooms == 0 || numPlayers == 0 || numWeapons == 0) {
			theAnswer = null;
		}
		else {
			Random rand = new Random();
			// Make sure deck is in order Room, Person, Weapon
			Collections.sort(deck, Comparator.comparing(Card::getType));
			// Sort into type lists
			List<Card> roomCards = deck.subList(0, numRooms);
			List<Card> playerCards = deck.subList(numRooms, numRooms + numPlayers);
			List<Card> weaponCards = deck.subList(numRooms + numPlayers, deck.size());

			theAnswer = new Solution(roomCards.get(rand.nextInt(roomCards.size())), playerCards.get(rand.nextInt(playerCards.size())), weaponCards.get(rand.nextInt((weaponCards.size()))));
		}
	}

	// Deal cards to players
	public void deal() {
		// Make sure solution is not null
		if(theAnswer != null) {
			// Remove the solution cards from the deck
			Card solutionRoom = theAnswer.getRoom();
			Card solutionPerson = theAnswer.getPerson();
			Card solutionWeapon = theAnswer.getWeapon();

			// Create a new list that holds the remaining cards
			ArrayList<Card> remainingCards = new ArrayList<Card>(deck);
			remainingCards.remove(solutionRoom);
			remainingCards.remove(solutionPerson);
			remainingCards.remove(solutionWeapon);

			// Shuffle the cards
			Collections.shuffle(remainingCards);

			// Deal cards to players
			int playerIndex = 0;
			int numPlayers = players.size();
			for (Card card : remainingCards) {
				players.get(playerIndex).updateHand(card);
				playerIndex = (playerIndex + 1) % numPlayers;
			}
		}

	}

	// Check to see if an accusation is correct
	public boolean checkAccusation(Solution solution) {
		return(theAnswer.getPerson() == solution.getPerson() && theAnswer.getRoom() == solution.getRoom() && theAnswer.getWeapon() == solution.getWeapon());
	}

	// Go through each player and see if they can disprove the suggestion made
	public Card handleSuggestion(Solution suggestion, Player suggestingPlayer) {
		Card cardToReturn = null;
		for(Player player : players) {
			if(player != suggestingPlayer) { // Make sure the player's own cards are not spoiled
				cardToReturn = player.disproveSuggestion(suggestion);
				if(cardToReturn != null) return cardToReturn;
			}
		}
		return null;// If no one can disprove it, return null
	}

	// Handle a turn when the button is clicked
	public void handleTurn() {
		// make sure current player's turn is finished
		if(!currentPlayer.isTurnFinished()) {
			JOptionPane.showMessageDialog(gameFrame, "Please finish your turn before clicking next");
			return;
		}

		advancePlayer();
		int roll = rollDice();
		calcTargets(getCell(currentPlayer.getRow(), currentPlayer.getCol()), roll);
		controlPanel.setTurn(currentPlayer, roll);

		if(currentPlayer instanceof HumanPlayer) {
			isHumanTurn = true;
			makeHumanTurn((HumanPlayer)currentPlayer);
		} else {
			isHumanTurn = false;
			makeComputerTurn((ComputerPlayer)currentPlayer);
		}

	}

	// Make a human's turn
	public void makeHumanTurn(HumanPlayer currentPlayer) {
		boardPanel.repaint(); // show highlighted cells
		currentPlayer.setTurnFinished(false); // wait for a board click

		// TODO C25 if in a room, make a suggestion
		// TODO C23 add slow moving animation


	}

	// Make a computer's turn
	public void makeComputerTurn(ComputerPlayer currentPlayer) {
		currentPlayer.setTurnFinished(false);
		// Make the computer move
		BoardCell selectedMove = currentPlayer.selectTarget(targets);
		boardPanel.movePlayer(selectedMove.getRow(), selectedMove.getCol());
		boardPanel.repaint();
		currentPlayer.setTurnFinished(true);
	}

	// Make the first turn of the game
	public void startFirstTurn() {
		int roll = rollDice();
		calcTargets(getCell(currentPlayer.getRow(), currentPlayer.getCol()), roll);
		controlPanel.setTurn(currentPlayer, roll);
		isHumanTurn = true;
		makeHumanTurn((HumanPlayer)currentPlayer);
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

	// Getter for room map
	public Map<Character, Room> getRoomMap() {
		return roomMap;
	}

	// Get numbers of cards for testing
	public int getNumRooms() {
		return numRooms;
	}
	public int getNumPlayers() {
		return numPlayers;
	}
	public int getNumWeapons() {
		return numWeapons;
	}
	public int getDeckSize() {
		return deckSize;
	}

	// Used for testing
	public void setSolution(Solution sol) {
		theAnswer = sol;
	}

	// Used for testing
	public void setPlayersArrayList(ArrayList<Player> players) {
		this.players = players;
	}

	// Get a random dice roll 1-6
	public int rollDice() {
		Random rand = new Random();
		return rand.nextInt(6) + 1;
	}

	// Advance to the next player
	public void advancePlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		currentPlayer = players.get(currentPlayerIndex);
	}

	public void setFrame(ClueGame frame){
		this.gameFrame = frame;
	}

	public void setControlPanel(GameControlPanel panel) {
		this.controlPanel = panel;
	}

	public void setBoardPanel(BoardPanel panel) {
		this.boardPanel = panel;
	}

	public void setCardsPanel(KnownCardsPanel panel) {
		this.cardsPanel = panel;
	}

	public boolean isHumanTurn() {
		return isHumanTurn;
	}

	public void setHumanTurn(boolean isHumanTurn) {
		this.isHumanTurn = isHumanTurn;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
}
