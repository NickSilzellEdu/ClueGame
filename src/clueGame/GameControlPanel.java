package clueGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/*
 * This class sets up the panel for the bottom part of the window, the game's control panel
 */
 
public class GameControlPanel extends JPanel{
	
	// Text fields
	private JTextField turnField;
	private JTextField rollField;
	private JTextField guessField;
	private JTextField guessResultField;
	
	// Setup layout for the panel
	public GameControlPanel() {
		
		// Grid layout -- 2 rows
		setLayout(new GridLayout(2, 0));
		
		// Top panel -- 1 row, 4 cols
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 4));
		
		// Sub panel from top panel -- 2 rows, 2 cols
		JPanel turnPanel = new JPanel();
		turnPanel.setLayout(new GridLayout(2, 2));
		
		// turnLabel
		JLabel turnLabel = new JLabel("Whose turn?");
		turnField = new JTextField(15);
		turnField.setEditable(false);
		
		// rollLabel
		JLabel rollLabel = new JLabel("Roll:");
		rollField = new JTextField(5);
		rollField.setEditable(false);
		
		turnPanel.add(turnLabel);
		turnPanel.add(turnField);
		turnPanel.add(rollLabel);
		turnPanel.add(rollField);
		
		// Sub panel for buttons
		JPanel buttonPanel = new JPanel();
		JButton accuseButton = new JButton("Make Accusation");
		JButton nextButton = new JButton("NEXT!");
		
		buttonPanel.add(accuseButton);
		buttonPanel.add(nextButton);
		
		// Add the sub-panels
		topPanel.add(turnPanel);
		topPanel.add(buttonPanel);
		add(topPanel);
		
		guessField = new JTextField();
		guessResultField = new JTextField();
		
		// Bottom panel -- 1 row, 2 cols
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(0, 2));
		
		// Bottom left guess panel
		JPanel bottomGuessPanel = new JPanel();
		bottomGuessPanel.add(guessField);
		
		// Bottom right panel, Guess Result
		JPanel bottomGuessResultPanel = new JPanel();
		bottomGuessResultPanel.add(guessResultField);
		
		// Add both left and right sides to bottom panel
		bottomPanel.add(bottomGuessPanel);
		bottomPanel.add(bottomGuessResultPanel);
		
		// Add top and bottom to frame
		add(bottomPanel);
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
		
		// test filling in the data
		panel.setTurn(new ComputerPlayer("Archer Queen", Color.white, 0, 0), 5);
		panel.setGuess( "I have no guess!");
		panel.setGuessResult( "So you have nothing?");
	}
}
