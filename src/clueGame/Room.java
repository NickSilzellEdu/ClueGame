package clueGame;

import java.util.ArrayList;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class represents a room in the game board
 */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Room {

	private String name; // The name of the room
	private BoardCell centerCell; // The cell where players will stand
	private BoardCell labelCell; // The cell where the room label will be displayed
	private Set<BoardCell> roomCells; // Set of all the cells in this room
	private List<Player> playersInRoom; // number of players inside this room

	// Constructor to create a room with the given name
	public Room(String name) {
		this.name = name;
		roomCells = new HashSet<BoardCell>();
		playersInRoom = new ArrayList<Player>();
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
	public void addPlayer(Player p) {
		if(!playersInRoom.contains(p)) playersInRoom.add(p);
	}

	// Remove a player form this room
	public void removePlayer(Player p) {
		playersInRoom.remove(p);
}

	// get number of players in a room
	public int getNumPlayers() {
		return playersInRoom.size();
	}
	
	public int getPlayerIndex(Player p) {
		return playersInRoom.indexOf(p);
	}
}
