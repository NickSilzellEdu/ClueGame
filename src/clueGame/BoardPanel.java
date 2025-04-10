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
		
		// Styling the room names text
		java.awt.Font clashFont = new java.awt.Font("Old English Text MT", java.awt.Font.BOLD, 20);
	    g.setFont(clashFont);
		
		// Draw the room names (will do later)
		board.getRoomMap().forEach((initial, room) -> {
            BoardCell labelCell = room.getLabelCell();
            if (labelCell != null) {
                int x = labelCell.getCol() * cellWidth;
                int y = labelCell.getRow() * cellHeight;
                g.setColor(Color.BLACK);
                g.drawString(room.getName(), x-15, y+25);
            }
        });
		
		
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
