package clueGame;

import javax.swing.JOptionPane;

/*
 * Created by Nick Silzell and Andrew Grimes on Mar 2 2025
 * This class represents the ClueGame board panel, a singleton class
 */

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;


public class BoardPanel extends JPanel {
	private Board board;
	private int cellWidth, cellHeight;
	private int numRows, numCols;

	public BoardPanel() {
		// Singleton board instance
		board = Board.getInstance();

		// set dimensions
		numRows = board.getNumRows();
		numCols = board.getNumColumns();
		cellWidth = getWidth() / numCols;
		cellHeight = getHeight() / numRows;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleBoardClick(e.getX(), e.getY());
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		numRows = board.getNumRows();
		numCols = board.getNumColumns();
		cellWidth = getWidth() / numCols;
		cellHeight = getHeight() / numRows;

		// Draw the cells and doors
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				BoardCell cell = board.getCell(row, col);
				cell.draw(g, row, col, cellWidth, cellHeight);
			}
		}

		// Draw all doors over the cells
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				// Draw door if necessary
				DoorDirection direction = board.getCell(row, col).getDoorDirection();	
				if(direction != DoorDirection.NONE) {
					int x = col * cellWidth;
					int y = row * cellHeight;
					g.setColor(new Color(101, 67, 33)); // Brown doors
					switch(direction) {
					case DoorDirection.UP:
						g.fillRect(x, y - cellHeight/5, cellWidth, cellHeight/5);
						g.drawRect(x, y - cellHeight/5, cellWidth, cellHeight/5);
						break;
					case DoorDirection.DOWN:
						g.fillRect(x, y + cellHeight, cellWidth, cellHeight/5);
						g.drawRect(x, y + cellHeight, cellWidth, cellHeight/5);
						break;
					case DoorDirection.RIGHT:
						g.fillRect(x + cellWidth, y, cellWidth/5, cellHeight);
						g.drawRect(x + cellWidth, y, cellWidth/5, cellHeight);
						break;
					case DoorDirection.LEFT:
						g.fillRect(x - cellWidth/5, y, cellWidth/5, cellHeight);
						g.drawRect(x - cellWidth/5, y, cellWidth/5, cellHeight);	
						break;
					default: // If direction is none to nothing
						break;
					}
				}
			}
		}

		// Styling the room names text
		java.awt.Font clashFont = new java.awt.Font("Old English Text MT", java.awt.Font.BOLD, 15);
		g.setFont(clashFont);

		// Draw the room names 
		board.getRoomMap().forEach((initial, room) -> {
			BoardCell labelCell = room.getLabelCell();
			if (labelCell != null) {
				int x = labelCell.getCol() * cellWidth;
				int y = labelCell.getRow() * cellHeight;
				g.setColor(Color.BLACK);
				g.drawString(room.getName(), x - cellWidth + 5, y + cellHeight);
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

	private void handleBoardClick(int x, int y) {
		// Find row and column of cell
		int row = y / cellHeight;
		int col = x / cellWidth;

		BoardCell clickedCell = board.getCell(row, col);
		// If it is a valid cell and the human's turn
		if(board.isHumanTurn() && board.getTargets().contains(clickedCell)) {
			movePlayer(row, col);
			board.getCurrentPlayer().setTurnFinished(true);
			repaint();
			}
		// Invalid click
		else if(board.isHumanTurn()) {
			JOptionPane.showMessageDialog(this, "Invalid cell, please click a highlited move");
		}
		// Not the player's turn
		else JOptionPane.showMessageDialog(this, "It is not your turn");
	}

	private void movePlayer(int newRow, int newCol) {
		Player player = board.getCurrentPlayer();
		player.setRow(newRow);
		player.setCol(newCol);

		// add an animation to move the player TODO

	}
	
	
}
