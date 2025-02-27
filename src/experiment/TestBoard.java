/*
 * Created by Nick Silzell and Andrwe Grimes on Feb 25 2025
 */

package experiment;
import java.util.Set;
import java.util.HashSet;
public class TestBoard {
	public TestBoard() {
		// INCOMPLETE
	}
	
	// Calculates legal targets for a move from startCell of length pathLength
	public void calcTargets(TestBoardCell startCell, int pathLength) {
		// INCOMPLETE
	}
	
	// Returns the cell from the board at row, col
	public TestBoardCell getCell(int row, int col) {
		// INCOMPLETE
		return new TestBoardCell(row, col);
	}
	
	// Gets the targets last created by calcTargest()
	public Set<TestBoardCell> getTargets(){
		return new HashSet<TestBoardCell>();
	}
}
