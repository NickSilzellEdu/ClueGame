package clueGame;

import java.util.Objects;

/*
 * This class represents a card in the game of clue, either a player, weapon, or room
 */
 
public class Card {
	private String cardName;
	private CardType type;
	
	// Card constructor to create a card with a name and type
	public Card(String cardName, CardType type) {
		this.cardName = cardName;
		this.type = type;
	}
	
	// Return the card name
	public String getCardName() {
		return "name"; // stub
	}
	
	// Return the card type
	public CardType getType() {
		return CardType.PERSON; // stub
	}
	
	// Return whether this card is equal to another
	@Override
	public boolean equals(Object target) {
		if (target == null) return false;
		return this == target;
		}
	
	// Returns a string respresentation of the card
	@Override
	public String toString() {
		return cardName + " (" + type + ")";
	}	
	
	// Used for hashmap
	@Override
	public int hashCode() {
		return Objects.hash(cardName, type);
	}

}
