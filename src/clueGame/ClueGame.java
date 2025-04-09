package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the ClueGame board into a GUI 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class ClueGame extends JFrame {
	private BoardPanel boardPanel;
	private GameControlPanel controlPanel;
	private KnownCardsPanel cardsPanel;
	
	public ClueGame() {
		// Setup the main window
		setTitle("Clash of Clans Clue Game");
		setSize(1000, 8000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// BoardPanel
		boardPanel = new BoardPanel();
		boardPanel.setPreferredSize(new Dimension(600, 600));
		add(boardPanel, BorderLayout.CENTER);
		
		// GameControlPanel
		controlPanel = new GameControlPanel();
		controlPanel.setPreferredSize(new Dimension(1000, 150));
		add(controlPanel, BorderLayout.SOUTH);
		
		// KnownCardsPanel
		cardsPanel = new KnownCardsPanel();
		cardsPanel.setPreferredSize(new Dimension(250, 800));
		add(cardsPanel, BorderLayout.EAST);
		
		// Everything is shown at once
		setVisible(true);
		setResizable(false); // Keep set aspect ratios

	}
	
	public static void main(String[] args) {
		Board board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
		new ClueGame();
	}
}
