package clueGame;

import java.util.HashSet;
import java.util.Set;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class represents a room in the game board
 */

public class Room {

	// The name of the room
	private String name;

	// The cell where players will stand
	private BoardCell centerCell;

	// The cell where the room label will be displayed
	private BoardCell labelCell;

	// Set of all the cells in this room
	private Set<BoardCell> roomCells;
	
	// number of players inside this room
	private int playersInRoom;

	// Constructor to create a room with the given name
	public Room(String name) {
		this.name = name;
		roomCells = new HashSet<BoardCell>();
		playersInRoom = 0;
	}

	// Returns the name of the room
	public String getName() {
		return name;
	}

	// Sets the name of the room
	public void setName(String name) {
		this.name = name;
	}

	// Returns the center cell of the room
	public BoardCell getCenterCell() {
		return centerCell;
	}

	// Sets the center cell of the room
	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	// Returns the label cell of the room
	public BoardCell getLabelCell() {
		return labelCell;
	}

	// Sets the label cell of the room
	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
	
	// Get the room cell set
	public Set<BoardCell> getRoomCells(){
		return roomCells;
	}
	
	// add a player to this room
	public void addPlayer() {
		playersInRoom++;
	}
	
	// Remove a player form this room
	public void removePlayer() {
		playersInRoom--;
	}
	
	// get number of players in a room
	public int getNumPlayers() {
		return playersInRoom;
	}
}
