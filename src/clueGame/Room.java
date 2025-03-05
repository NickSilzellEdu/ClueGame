package clueGame;

public class Room {
	private String name;
	private BoardCell centerCell; // The cell where players will stand
	private BoardCell labelCell; // The cell where the room label will be
	
	public Room(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BoardCell getCenterCell() {
		return centerCell;
	}
	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}
	public BoardCell getLabelCell() {
		return labelCell;
	}
	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
}
