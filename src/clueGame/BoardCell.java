package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class represents the Clue Game board cells
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class BoardCell {
	private int row;
	private int col;
	private char initial;
	private DoorDirection doorDirection;
	private boolean isLabel;
	private boolean isRoomCenter;
	private boolean isRoom;
	private boolean isOccupied;
	private char secretPassage;
	private Set<BoardCell> adjList;

	// Constructor to initialize a board cell with row and column values
	public BoardCell(int row, int col) {
		this.row = row;
		this.col = col;
		this.initial = ' ';
		this.isRoom = false;
		this.doorDirection = DoorDirection.NONE;
		this.isLabel = false;
		this.isRoomCenter = false;
		this.secretPassage = '\0';
		this.adjList = new HashSet<BoardCell>();
	}

	// Draw this cell on the board
	public void draw(Graphics g, int row, int col, int cellWidth, int cellHeight) {
		int x = col * cellWidth;
		int y = row * cellHeight;

		// Set a color based on cell type
		if (initial == 'W') {
			g.setColor(new Color(154, 205, 50)); // Green for grass
		} else if(initial == 'X') {
			g.setColor(new Color(101, 67, 33)); // Brown for walls
		}
		else if (isRoom()) {
			g.setColor(new Color(222, 184, 135)); // Beige for rooms
		}

		g.fillRect(x, y, cellWidth, cellHeight);
		g.setColor(Color.BLACK); // Outline
		g.drawRect(x, y, cellWidth, cellHeight);

	}

	// Returns the row index of the cell
	public int getRow() {
		return row;
	}

	// Returns the column index of the cell
	public int getCol() {
		return col;
	}

	// Returns the room initial of the cell
	public char getInitial() {
		return initial;
	}

	// Sets the room initial of the cell
	public void setInitial(char initial) {
		this.initial = initial;
	}

	// Returns the door direction of the cell
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	// Sets the door direction of the cell
	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	// Returns true if the cell contains a label
	public boolean isLabel() {
		return isLabel;
	}

	// Sets whether the cell contains a label
	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}

	// Returns true if the cell is the center of a room
	public boolean isRoomCenter() {
		return isRoomCenter;
	}

	// Sets whether the cell is the center of a room
	public void setRoomCenter(boolean isRoomCenter) {
		this.isRoomCenter = isRoomCenter;
	}

	// Returns the secret passage character of the cell
	public char getSecretPassage() {
		return secretPassage;
	}

	// Sets the secret passage character of the cell
	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	// Returns true if the cell is a doorway
	public boolean isDoorway() {
		return (doorDirection != DoorDirection.NONE);
	}

	// Adds an adjacent cell to the set of adjacent cells
	public void addAdjacency(BoardCell adj) {
		adjList.add(adj);
	}

	// Returns the set of adjacent cells
	public Set<BoardCell> getAdjList() {
		return adjList;
	}

	// Sets whether the cell is part of a room
	public void setIsRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}

	// Returns true if the cell is part of a room
	public boolean isRoom() {
		return isRoom;
	}

	// Sets whether the cell is currently occupied
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	// Returns true if the cell is currently occupied
	public boolean getOccupied() {
		return isOccupied;
	}
}
