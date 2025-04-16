package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class is used to create a players habits
 */

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
	private boolean turnFinished;
	private int x, y;
	
	public Player(String name, Color color, int row, int col) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
		this.hand = new HashSet<Card>();
		this.seen = new HashSet<Card>();
		this.turnFinished = true;
	}
	
	// Each player adds a card to their hand
    public void updateHand(Card card) {
    	hand.add(card);
    }
	
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
    
    public String getName() {
    	return name;
    }
    
    public Color getColor() {
    	return color;
    }
    
    // For testing, not to be used in game code
    public void setRow(int row) {
    	this.row = row;
    }
    
    public void setCol(int col) {
    	this.col = col;
    }
    
    public void setTurnFinished(boolean finished) {
		this.turnFinished = finished;
	}
	
	public boolean isTurnFinished() {
		return turnFinished;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int row, int col, int cellWidth, int cellHeight) {
		this.row = row;
		this.col = col;
		this.x = col * cellWidth;
		this.y = row * cellHeight;
	}
}
