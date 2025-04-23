package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class sets up the ClueGame board into a GUI 
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;
import java.awt.Color;
import java.util.Random;
import javax.sound.sampled.AudioInputStream; // for bg music
import javax.sound.sampled.AudioSystem; // for bg music
import javax.sound.sampled.Clip; // for bg music

public class ClueGame extends JFrame {
	private static BoardPanel boardPanel;
	private static GameControlPanel controlPanel;
	private static KnownCardsPanel cardsPanel;
	private static Clip backgroundClip;

	// Stop main music once an accusation is submitted
	public static void stopMusic() {
		if (backgroundClip != null && backgroundClip.isRunning()) {
			backgroundClip.stop();
			backgroundClip.close();
		}
	}
	
	public ClueGame() {
		
		// Setup the main window
		setTitle("Clash of Clans Clue Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BoardPanel
		boardPanel = new BoardPanel();
		boardPanel.setPreferredSize(new Dimension(500, 500));
		JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 25));
		centerWrapper.setBackground(new Color(140, 180, 100));
		centerWrapper.add(boardPanel);
		add(centerWrapper, BorderLayout.CENTER);

		// GameControlPanel
		controlPanel = new GameControlPanel();
		controlPanel.setPreferredSize(new Dimension(1000, 150));
		add(controlPanel, BorderLayout.SOUTH);

		// KnownCardsPanel
		cardsPanel = new KnownCardsPanel();
		cardsPanel.setPreferredSize(new Dimension(250, 800));
		add(cardsPanel, BorderLayout.EAST);

		// Everything is shown at once
		pack();
		setSize(950, 825);
		setLocationRelativeTo(null);
		setVisible(true);
		
		// Add bg music -- can play any of 3 songs
		String[] tracks = {
		    "home_music_part_1.wav",
		    "home_music_part_2.wav",
		    "home_music_part_3.wav"
		};
		
		// Randomize the played song
		String selected = tracks[new Random().nextInt(tracks.length)];
		try {
		    AudioInputStream ais = AudioSystem.getAudioInputStream(
		        getClass().getResource(selected)
		    );
		    
		    Clip clip = AudioSystem.getClip();
		    clip.open(ais);
		    clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop forever
		    backgroundClip = clip;
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException{
		Board board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();	
		
		ClueGame game = new ClueGame();
		board.setFrame(game);
		board.setControlPanel(controlPanel);
		board.setBoardPanel(boardPanel);
		board.setKnownCardsPanel(cardsPanel);
		
		// Present a welcome screen
		JOptionPane.showMessageDialog(game, "You are the " + board.getPlayers().get(0).getName() + ". Can you find the solution before the Computer Players?", "Welcome to Clue", JOptionPane.INFORMATION_MESSAGE);
		board.startFirstTurn();
	}
}
