package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the ClueGame board into a GUI 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClueGame extends JFrame {
	private BoardPanel boardPanel;
	private GameControlPanel controlPanel;
	private KnownCardsPanel cardsPanel;

	public ClueGame() {
		// Setup the main window
		setTitle("Clash of Clans Clue Game");
		setSize(900, 800);
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
	}

	public static void main(String[] args) {
		ClueGame game = new ClueGame();
		Board board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();	
		
		// Present a welcome screen
		JOptionPane.showMessageDialog(game, "You are the " + board.getPlayers().get(0).getName() + ". Can you find the solution before the Computer Players?", "Welcome to Clue", JOptionPane.INFORMATION_MESSAGE);
		
		board.playClue(game); // Play the game
	}

}
