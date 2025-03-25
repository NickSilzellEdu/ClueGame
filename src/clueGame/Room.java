package clueGame;

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
    
    // Constructor to create a room with the given name
    public Room(String name) {
        this.name = name;
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
}
