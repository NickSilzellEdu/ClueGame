package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes on Mar 2 2025
 * This class represents the ClueGame board panel, a singleton class
 */

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;


public class BoardPanel extends JPanel {
	private Board board;
	
	public BoardPanel() {
		// Singleton board instance
		board = Board.getInstance();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Cell sized, based on panel size
		int numRows = board.getNumRows();
		int numCols = board.getNumColumns();
		int cellWidth = getWidth() / numCols;
		int cellHeight = getHeight() / numRows;
		
		// Draw the cells
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				BoardCell cell = board.getCell(row, col);
				cell.draw(g, row, col, cellWidth, cellHeight);
			}
		}
		
		// Draw the room names (will do later)
		
		// Draw the players
		board.getPlayers().forEach(player -> {
			int row = player.getRow();
			int col = player.getCol();
			int x = col * cellWidth;
			int y = row * cellHeight;
			g.setColor(player.getColor());
			g.fillOval(x, y, cellWidth, cellHeight);
		});
	}
}
