package clueGame;

import java.util.HashSet;
import java.util.Set;
import java.awt.Color;

public abstract class Player {
	private Set<Card> hand;
	private String name;
	private Color color;
	private int row;
	private int col;
	
	public Player(String name, Color color, int row, int col) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		this.hand = new HashSet<Card>();
	}
	
	public abstract void updateHand(Card card);
	
	// Return this player's hand
	public Set<Card> getHand(){
		return this.hand;
	}
}
