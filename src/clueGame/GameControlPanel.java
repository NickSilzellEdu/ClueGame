package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the panel for the bottom part of the window, the game's control panel
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Random;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GameControlPanel extends JPanel{

	// Text fields
	private JTextField turnField;
	private JTextField rollField;
	private JTextField guessField;
	private JTextField guessResultField;

	// for access to board's info
	private static Board board;

	// Setup layout for the panel
	public GameControlPanel() {

		// Set layout for this panel
		setLayout(new GridLayout(2, 0));

		// Create all panels first
		JPanel topPanel = new JPanel(new GridLayout(1, 4)); // Top panel
		JPanel turnPanel = new JPanel(new GridLayout(2, 1)); // Inside topPanel 
		JPanel rollPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Inside topPanel

		JPanel bottomPanel = new JPanel(new GridLayout(1, 2)); // Bottom panel
		JPanel bottomGuessPanel = new JPanel(); // Inside bottomPanel
		JPanel bottomGuessResultPanel = new JPanel(); // Inside bottomPanel

		// Create all components
		JLabel turnLabel = new JLabel("Whose turn?");
		turnField = new JTextField(15);

		JLabel rollLabel = new JLabel("Roll:");
		rollField = new JTextField(5);

		JButton accuseButton = new JButton("Make Accusation");
		JButton nextButton = new JButton("NEXT!");

		guessField = new JTextField(20);
		guessResultField = new JTextField(20);

		// Add components to panels
		turnPanel.add(turnLabel);
		turnPanel.add(turnField);
		rollPanel.add(rollLabel);
		rollPanel.add(rollField);

		// Turn and Roll panels
		topPanel.add(turnPanel);
		topPanel.add(rollPanel);

		// Two buttons
		topPanel.add(accuseButton);
		topPanel.add(nextButton);

		// Bottom panels
		bottomGuessPanel.add(guessField);
		bottomGuessResultPanel.add(guessResultField);
		bottomPanel.add(bottomGuessPanel);
		bottomPanel.add(bottomGuessResultPanel);

		// Add main panels to layout
		add(topPanel);
		add(bottomPanel);

		// Make sure fields cannot be changed
		turnField.setEditable(false);
		rollField.setEditable(false);
		guessField.setEditable(false);
		guessResultField.setEditable(false);

		// Add colors to buttons
		accuseButton.setBackground(Color.LIGHT_GRAY);
		accuseButton.setOpaque(true);
		accuseButton.setBorderPainted(false);
		nextButton.setBackground(Color.LIGHT_GRAY);
		nextButton.setOpaque(true);
		nextButton.setBorderPainted(false);

		// Add labels to bottom panels
		bottomGuessPanel.setLayout(new BorderLayout());
		bottomGuessPanel.setBorder(BorderFactory.createTitledBorder("Guess"));
		bottomGuessPanel.add(guessField, BorderLayout.CENTER);
		bottomGuessResultPanel.setLayout(new BorderLayout());
		bottomGuessResultPanel.setBorder(BorderFactory.createTitledBorder("Guess Result"));
		bottomGuessResultPanel.add(guessResultField, BorderLayout.CENTER);

		//TODO25: add accuseButton action listener

		nextButton.addActionListener(e -> Board.getInstance().handleTurn()); 

	}

	// Set current player's turn field
	public void setTurn(Player player, int roll) {
		turnField.setText(player.getName());
		turnField.setBackground(player.getColor());
		rollField.setText(Integer.toString(roll));
	}

	// Set the guess field
	public void setGuess(String message) {
		guessField.setText(message);
	}

	// Set the guess result field
	public void setGuessResult(Card card) {
		guessResultField.setText(card.getCardName());	
		KnownCardsPanel.setFieldBackgroundFromCard(guessResultField, card);
	}
	public void setGuessResult(String message) {
		guessResultField.setText(message);
	}

	public static void main(String[] args) {
		GameControlPanel panel = new GameControlPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(750, 180);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		// Initialize Fields with board info
		board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();
		panel.setTurn(board.getPlayers().get(0), 6);// Get the first player and set roll to 6

		// test filling in the data
		panel.setGuess( "I have no guess!");
		panel.setGuessResult("So you have nothing?");
	}
}
