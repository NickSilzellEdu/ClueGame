package clueGame;

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

	private BoardCell(int row, int col) {
		this.row = row;
		this.col = col;
		this.initial = ' ';
		this.doorDirection = DoorDirection.NONE;
		this.isLabel = false;
		this.isRoomCenter = false;
		this.secretPassage = '\0';
		this.adjList = new HashSet<BoardCell>();
	}

	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	public char getInitial() {
		return initial;
	}
	public void setInitial(char initial) {
		this.initial = initial;
	}
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}
	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}
	public boolean isLabel() {
		return isLabel;
	}
	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}
	public boolean isRoomCenter() {
		return isRoomCenter;
	}
	public void setRoomCenter(boolean isRoomCenter) {
		this.isRoomCenter = isRoomCenter;
	}
	public char getSecretPassage() {
		return secretPassage;
	}
	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}
	public boolean isDoorway() {
		return (doorDirection != DoorDirection.NONE);
	}
	public void addAdjacency(BoardCell adj) {
		adjList.add(adj);
	}
	public Set<BoardCell> getAdjList() {
		return adjList;
	}
	public void setIsRoom(boolean isRoom) {
		this.isRoom = isRoom;	
	}
	public boolean isRoom() {
		return isRoom;
	}
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	public boolean getOccupied() {
		return isOccupied;
	}	
}
