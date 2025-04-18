package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the panel for the known cards panel
 */

import java.awt.GridLayout;
import java.util.Collections;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
		if (sectionPanel != null) {

			// If the card is seen, update the color to the player who has it
			if(!inHand) setFieldBackgroundFromCard(cardField, card);

			cardField.setEditable(false);
			sectionPanel.add(cardField);
			sectionPanel.revalidate();
			sectionPanel.repaint();
		}
	}

	public static void setFieldBackgroundFromCard(JTextField cardField, Card card) {
		Color cardColor = null;
		for(Player player : board.getPlayers()) {
			if(player.getHand().contains(card)) { 
				cardColor = player.getColor();
				break;
			}
		}

		if(cardColor != null) cardField.setBackground(cardColor);
	}
}
