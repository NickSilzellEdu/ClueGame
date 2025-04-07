package clueGame

import java.awt.BorderLayout;
import javax.swing.JFrame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the ClueGame board into a GUI 
 */

public class ClueGame extends JFrame {
	private BoardPanel boardPanel;
	private GameControlPanel controlPanel;
	private KnownCardsPanel cardsPanel
	
	public ClueGame() {
		// Setup the main window
		setTitle("Clash of Clans Clue Game");
		setSize(1000, 8000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// BoardPanel
		boardPanel = new BoardPanel();
		add(boardPanel, BorderLayout.CENTER);
		
		// GameControlPanel
		controlPanel = new GameControlPanel();
		add(controlPanel, BorderLayout.SOUTH);
		
		// KnownCardsPanel
		cardsPanel = new KnownCardsPanel();
		add(cardsPanel, BorderLayout.EAST);
		
		// Everything is shown at once
		setVisible(true);
	}
	
	public static void main(String[] args) {
		Board board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
		new ClueGame();
	}
}
