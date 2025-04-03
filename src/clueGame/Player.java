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
	private Set<Card> seen;
	
	public Player(String name, Color color, int row, int col) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		this.hand = new HashSet<Card>();
		this.seen = new HashSet<Card>();
	}
	
	// Each player adds a card to their hand
    public abstract void updateHand(Card card);
	
    // Each player will try to disprove a suggestion
    public abstract Card disproveSuggestion(Solution suggestion);
    
	// Return this player's hand
	public Set<Card> getHand(){
		return this.hand;
	}
	
	// Add a card to the seen list
    public void addSeen(Card card) {
        seen.add(card);
    }
    
    public Set<Card> getSeen() {
    	return seen;
    }
    
    public int getRow() {
    	return row;
    }
    
    public int getCol() {
    	return col;
    }
}
