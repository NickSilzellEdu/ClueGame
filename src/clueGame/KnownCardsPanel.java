package clueGame;

import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the panel for the known cards panel
 */

public class KnownCardsPanel extends JPanel {
	
	// Hand and seen cards
	private JPanel handPanel;
	private JPanel seenPanel;
	
	// Setup layout for the panel
	public KnownControlPanel() {
		
		// Set layout for this panel
		setLayout(new GridLayout(2, 1));
		
		// Create all panels first
		JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel handPanel.setBorder(new TitledBorder(new EtchedBorder(), "My Cards"));
		
		JPanel seenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel seenPanel.setBorder(new TitledBorder(new EtchedBorder(), "Seen Cards"));
		
		// Add panels to layout
		add(handPanel);
		add(seenPanel);
	}
}