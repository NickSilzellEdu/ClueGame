package clueGame;

import java.util.Set;

public abstract class Player {
	private Set<Card> hand;
	private String name;
	private Color color;
	
	public abstract void updateHand(Card card);
	
	// Return this player's hand
	public Set<Card> getHand(){
		return this.hand;
	}
}
