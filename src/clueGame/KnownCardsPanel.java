package clueGame;

import java.awt.GridLayout;
import java.util.Collections;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the panel for the known cards panel
 */

public class KnownCardsPanel extends JPanel {
	private static Board board;

	// Panels that can be updated dynamically
	private JPanel peopleInHand = new JPanel();
	private JPanel peopleSeen = new JPanel();
	private JPanel roomsInHand = new JPanel();
	private JPanel roomsSeen = new JPanel();
	private JPanel weaponsInHand = new JPanel();
	private JPanel weaponsSeen = new JPanel();


	// Setup Panel
	public KnownCardsPanel() {

		// Set main layout in three sections
		setLayout(new GridLayout(3, 1));
		setBorder(BorderFactory.createTitledBorder("Known Cards"));

		// Add a panel for each type
		add(createTypePanel("People", peopleInHand, peopleSeen));
		add(createTypePanel("Rooms", roomsInHand, roomsSeen));
		add(createTypePanel("Weapons", weaponsInHand, weaponsSeen));

		// Initialize board for player list
		board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();

	}

	// Helper function to build a type panel with a dynamic layout
	private JPanel createTypePanel(String title, JPanel inHandPanel, JPanel seenPanel) {
		JPanel typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
		typePanel.setBorder(BorderFactory.createTitledBorder(title));

		// Setup layout for inner sections
		inHandPanel.setLayout(new BoxLayout(inHandPanel, BoxLayout.Y_AXIS));
		seenPanel.setLayout(new BoxLayout(seenPanel, BoxLayout.Y_AXIS));

		// Add labels 
		inHandPanel.add(new JLabel("In Hand:"));
		seenPanel.add(new JLabel("Seen:"));

		typePanel.add(inHandPanel);
		typePanel.add(seenPanel);

		return typePanel;
	}

	// Add a card to a section
	public void addCard(Card card, boolean inHand) {
		JPanel sectionPanel;
		JTextField cardField = new JTextField(card.getCardName());
		switch(card.getType()) {
		case CardType.PERSON:
			// Handle section
			if(inHand) sectionPanel = peopleInHand;
			else sectionPanel = peopleSeen;

			// If the card is a person, add the color
			Color cardColor = Color.white; // white for default
			for(Player player : board.getPlayers()) {
				if (player.getName().equals(card.getCardName())) cardColor = player.getColor();	
			}
			cardField.setBackground(cardColor);
			break;
		case CardType.ROOM:
			if(inHand) sectionPanel = roomsInHand;
			else sectionPanel = roomsSeen;
			break;
		case CardType.WEAPON:
			if(inHand) sectionPanel = weaponsInHand;
			else sectionPanel = weaponsSeen;
			break;
		default:
			sectionPanel = null;
		}

		if (sectionPanel != null)
		{
			cardField.setEditable(false);
			sectionPanel.add(cardField);
			sectionPanel.revalidate();
			sectionPanel.repaint();
		}
	}

	// Update the 
	public static void main(String[] args) {
		KnownCardsPanel panel = new KnownCardsPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(200, 800);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible
		
		// Test using board values, it was initialized in KnownCards constructor
		Board board = Board.getInstance();		
		
		Collections.shuffle(board.getDeck());
		// Add 4 random cards to in hand
		for(int i = 0; i < 4; i++) {
			panel.addCard(board.getDeck().get(i), true);
		}
		
		// Add 8 random cards to seen, starting from 4 so there is no repeats
		for(int i = 4; i < 12; i++) {
			panel.addCard(board.getDeck().get(i), false);
		}

	}
}