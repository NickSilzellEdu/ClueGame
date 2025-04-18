package clueGame;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class represents the ClueGame board panel, a singleton class
 */

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;

public class BoardPanel extends JPanel {
	private static Board board;
	private int cellWidth; 
	private int cellHeight;
	private int numRows;
	private int numCols;

	public BoardPanel() {

		// Singleton board instance
		board = Board.getInstance();

		// set dimensions
		numRows = board.getNumRows();
		numCols = board.getNumColumns();
		cellWidth = getWidth() / numCols;
		cellHeight = getHeight() / numRows;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleBoardClick(e.getX(), e.getY());
			}
		});

		// Wait until board is resized to initialize/set player x and y coordinates
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				cellWidth = getWidth() / numCols;
				cellHeight = getHeight() / numRows;

				for (Player player : board.getPlayers()) {
					player.setX(player.getCol() * cellWidth);
					player.setY(player.getRow() * cellHeight);
				}
				repaint();
			}
		});
	}

	// Board sizing
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 600);
	}
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// add a spacer
	    g.setColor(new Color(140, 180, 100));
	    g.fillRect(0, 0, getWidth(), getHeight());

		g.fillRect(0, 0, getWidth(), getHeight());

		// now go on to draw your 600×600 board in the top‑left corner…
		int boardW = getPreferredSize().width;
		int boardH = getPreferredSize().height;
		int cellW = boardW / numCols;
		int cellH = boardH / numRows;

		numRows = board.getNumRows();
		numCols = board.getNumColumns();
		cellWidth = getWidth() / numCols;
		cellHeight = getHeight() / numRows;

		// Draw the cells and secret passages
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				BoardCell cell = board.getCell(row, col);
				// draw an S character in secret passage cells
				if(cell.getSecretPassage() != '\0') {
					drawSecretDoor(g, row, col);
				}
				else cell.draw(g, row, col, cellWidth, cellHeight); // normal cell
			}
		}

		// highlight target cells if necessary
		if(board.isHumanTurn()) highlightTargets(g);

		// Draw all doors over the cells
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				// Draw door if necessary
				DoorDirection direction = board.getCell(row, col).getDoorDirection();	

				if(direction != DoorDirection.NONE) {
					int x = col * cellWidth;
					int y = row * cellHeight;
					g.setColor(new Color(101, 67, 33)); // Brown doors
					switch(direction) {
					case DoorDirection.UP:
						g.fillRect(x, y - cellHeight/5, cellWidth, cellHeight/5);
						g.drawRect(x, y - cellHeight/5, cellWidth, cellHeight/5);
						break;

					case DoorDirection.DOWN:
						g.fillRect(x, y + cellHeight, cellWidth, cellHeight/5);
						g.drawRect(x, y + cellHeight, cellWidth, cellHeight/5);
						break;

					case DoorDirection.RIGHT:
						g.fillRect(x + cellWidth, y, cellWidth/5, cellHeight);
						g.drawRect(x + cellWidth, y, cellWidth/5, cellHeight);
						break;

					case DoorDirection.LEFT:
						g.fillRect(x - cellWidth/5, y, cellWidth/5, cellHeight);
						g.drawRect(x - cellWidth/5, y, cellWidth/5, cellHeight);	
						break;

					default: // If direction is none do nothing
						break;
					}
				}
			}
		}

		// Draw the players in order of how recent they arrived in a room if necessary
		ArrayList<Player> playersToDraw = new ArrayList<>(board.getPlayers());
		playersToDraw.sort((p1, p2) -> Integer.compare(p1.getDrawPriority(), p2.getDrawPriority()));

		playersToDraw.forEach(player -> {
			int x = player.getX();
			int y = player.getY();
			g.setColor(player.getColor());
			g.fillOval(x, y, cellWidth, cellHeight);
		});

		// Styling the room names text
		java.awt.Font clashFont = new java.awt.Font("Old English Text MT", java.awt.Font.BOLD, 15);
		g.setFont(clashFont);

		// Draw the room names 
		board.getRoomMap().forEach((_, room) -> {
			BoardCell labelCell = room.getLabelCell();
			if (labelCell != null) {
				int x = labelCell.getCol() * cellWidth;
				int y = labelCell.getRow() * cellHeight;
				g.setColor(Color.BLACK);
				g.drawString(room.getName(), x - cellWidth + 5, y + cellHeight + 7);
			}
		});
	}

	private void handleBoardClick(int x, int y) {

		// Find row and column of cell
		int numRows = board.getNumRows();
	    int numCols = board.getNumColumns();
	    int cellW = getWidth()  / numCols;
	    int cellH = getHeight() / numRows;

	    // avoid division by zero
	    if (cellW == 0 || cellH == 0) return;

	    int col = x / cellW;
	    int row = y / cellH;

	    // guard against clicks in the “gutter” or off‑board
	    if (row < 0 || row >= numRows || col < 0 || col >= numCols) return;
		BoardCell clickedCell = board.getCell(row, col);

		// If it is a valid cell and the human's turn
		if(board.isHumanTurn() && board.getTargets().contains(clickedCell)) {
			movePlayer(row, col);
			board.setHumanTurn(false);
			board.getCurrentPlayer().setTurnFinished(true);
			repaint();
		}

		// If cell clicked is in a room, and the room's center is a target, move the player to the room center
		else if(board.isHumanTurn() && clickedCell.isRoom() && board.getTargets().contains(board.getRoom(clickedCell).getCenterCell())) {
			BoardCell centerCell = board.getRoom(clickedCell).getCenterCell();
			movePlayer(centerCell.getRow(), centerCell.getCol());
			board.setHumanTurn(false);
			board.getCurrentPlayer().setTurnFinished(true);
			repaint();
		}

		// Invalid click
		else if(board.isHumanTurn()) {
			JOptionPane.showMessageDialog(this, "Invalid cell, please click a highlighted move");
		}

		// Not the player's turn
		else JOptionPane.showMessageDialog(this, "It is not your turn, please click NEXT!");
	}

	// Animate player movement and update location
	public void movePlayer(int newRow, int newCol) {
		Player player = board.getCurrentPlayer();
		int startX = player.getCol() * cellWidth;
		int startY = player.getRow() * cellHeight;
		final int[] endX = {newCol * cellWidth}; // Use a final array to get around java's complaints for possible change
		int endY = newRow * cellHeight;
		int steps = 30; // Number of frames for movement
		int delay = 10; // Delay between frames in ms

		// If new cell is an occupied room center, offshift x appropriately so they don't cover eachother
		if(board.getCell(newRow, newCol).isRoomCenter()) {
			Room occupiedRoom = board.getRoom(board.getCell(newRow, newCol));
			endX[0] = endX[0] + 5 * (occupiedRoom.getNumPlayers());
			occupiedRoom.addPlayer();
		}

		// If a player is leaving a room, subtract the amount of players
		if(board.getCell(player.getRow(), player.getCol()).isRoomCenter()) board.getRoom(board.getCell(player.getRow(), player.getCol())).removePlayer();

		// Find change in x and y
		double dx = (endX[0] - startX) / (double) steps;
		double dy = (endY - startY) / (double) steps;

		// Update occupied cells
		board.getCell(player.getRow(), player.getCol()).setOccupied(false);
		board.getCell(newRow, newCol).setOccupied(true);

		// Use timer to space out steps
		Timer timer = new Timer(delay, null);
		final int[] count = {0};

		timer.addActionListener(e -> {
			if (count[0] < steps) {
				player.setX((int) (startX + dx * count[0]));
				player.setY((int) (startY + dy * count[0]));
				count[0]++;
				repaint();

			} else { // Finish movement at exact location
				player.setRow(newRow);
				player.setCol(newCol);
				player.setX(endX[0]);
				player.setY(endY);
				player.bumpDrawPriority();
				repaint();
				timer.stop();

				// If player is in a room, make a suggestion
				if(player instanceof HumanPlayer && board.getCell(newRow, newCol).isRoomCenter()) makeSuggestion();
			}
		});
		timer.start();
	}

	// Highlight all walkway targets, and valid rooms
	public void highlightTargets(Graphics g) {
		HashSet<BoardCell> targetedCenters = new HashSet<BoardCell>();

		// Highlight each target cell, and add valid room centers
		for (BoardCell cell : board.getTargets()) {
			int x = cell.getCol() * cellWidth;
			int y = cell.getRow() * cellHeight;
			g.setColor(new Color(0, 175, 0)); // Dark green highlighted rooms
			g.fillRect(x, y, cellWidth, cellHeight);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, cellWidth, cellHeight);
			if(cell.isRoomCenter()) targetedCenters.add(cell);
		}

		// add thick outline when player can move into room
		Graphics2D g2 = (Graphics2D) g;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2));

		// Highlight all cells of a room based off it's room center
		for(BoardCell cell : targetedCenters) {
			Character roomChar = cell.getInitial();
			for(BoardCell roomCell : board.getRoomMap().get(roomChar).getRoomCells()) {
				int x = roomCell.getCol() * cellWidth;
				int y = roomCell.getRow() * cellHeight;
				g.setColor(new Color(0, 175, 0)); // Dark green highlighted rooms
				g.fillRect(x, y, cellWidth, cellHeight);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, cellWidth, cellHeight);
			}
		}

		// Restore the original stroke
		g2.setStroke(oldStroke);
	}

	// Draw the symbol for a secret passage door
	public void drawSecretDoor(Graphics g, int row, int col) {
		int x = col * cellWidth;
		int y = row * cellHeight;

		// Draw beige room background
		g.setColor(new Color(222, 184, 135));
		g.fillRect(x, y, cellWidth, cellHeight);

		// Draw black border
		g.setColor(Color.BLACK);
		g.drawRect(x, y, cellWidth, cellHeight);

		// Draw black door opening on right and top
		g.fillRect(x + cellWidth - cellWidth / 5, y, cellWidth / 5, cellHeight); // right bar
		int[] xPoints = { x + cellWidth, x + cellWidth, x + cellWidth / 2 };
		int[] yPoints = { y, y + cellHeight / 5, y };
		g.fillPolygon(xPoints, yPoints, 3);

		// Draw black 'S' centered
		g.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15));
		FontMetrics fm = g.getFontMetrics();
		String text = "S";
		int textWidth = fm.stringWidth(text);
		int textHeight = fm.getAscent();
		g.drawString("S", x + (cellWidth - textWidth) / 2, y + (cellHeight + textHeight) / 2 - 1);
	}

	// Prompt user for a suggestion
	public Solution showSuggestion() {
		JPanel suggestionPanel = new JPanel(new GridLayout(4, 2, 10, 10));

		// Add room field, fixed
		suggestionPanel.add(new JLabel("Current Room:"));
		JTextField roomField = new JTextField(board.getRoom(board.getCurrentPlayer()).getName()); // Get the name of the room player is in
		roomField.setEditable(false);
		suggestionPanel.add(roomField);

		// Add person field, dropdown
		suggestionPanel.add(new JLabel("Person:"));
		JComboBox<Card> personDropDown = new JComboBox<Card>(board.getPersonCards().toArray(new Card[0]));
		suggestionPanel.add(personDropDown);

		// Add weapon field
		suggestionPanel.add(new JLabel("Weapon:"));
		JComboBox<Card> weaponDropDown = new JComboBox<Card>(board.getWeapons().toArray(new Card[0]));
		suggestionPanel.add(weaponDropDown);

		int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this), suggestionPanel, "Make a suggestion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		// Process user suggesiton input
		if(result == JOptionPane.OK_OPTION) {
			return new Solution(board.getRoomCard(board.getRoom(board.getCurrentPlayer())), (Card)personDropDown.getSelectedItem(), (Card)weaponDropDown.getSelectedItem());
		}
		else return null; // If user hit cancel
	}

	// Update board proplerly after suggestion
	public void makeSuggestion() {
		Player currentPlayer = board.getCurrentPlayer();
		GameControlPanel controlPanel = board.getControlPanel();
		if(board.getCell(currentPlayer.getRow(), currentPlayer.getCol()).isRoomCenter()) {
			Solution suggestion = showSuggestion();
			if(suggestion != null) {
				Card disprovingCard = board.handleSuggestion(suggestion, currentPlayer);
				controlPanel.setGuess(suggestion.getRoom().getCardName() + ", " + suggestion.getPerson().getCardName() + ", " + suggestion.getWeapon().getCardName());
				// Update guess result based on whether or not there is a disproving card
				if(disprovingCard == null) {
					controlPanel.setGuessResult("No one can disprove your suggestion!");
				}
				else {
					controlPanel.setGuessResult(disprovingCard);
				}
			}
		}
	}
}
