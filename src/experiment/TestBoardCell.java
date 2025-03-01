/*
 * Created by Nick Silzell and Andrew Grimes on Feb 25 2025
 */

package experiment;

import java.util.Set;
import java.util.HashSet;

public class TestBoardCell {
	private int row, col;
	private Boolean isRoom, isOccupied;
	Set<TestBoardCell> adjList;

	public TestBoardCell(int row, int col) {
		adjList = new HashSet<TestBoardCell>();
		this.row = row;
		this.col = col;
		this.isRoom = false;
		this.isOccupied = false;
	}
	
	// Add a cell to this cell's adjacency list
	public void addAdjacency(TestBoardCell cell) {
		adjList.add(cell);
	}
	
	// Return adjacency list for this cell
	public Set<TestBoardCell> getAdjList(){
		return adjList;
	}
	
	public void setIsRoom(boolean isRoom) {
		this.isRoom = isRoom;	}
	
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
