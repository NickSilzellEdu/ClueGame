package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class represents a card in the game of clue, either a player, weapon, or room
 */

import java.util.Objects;

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
		return cardName;
	}

	// Return the card type
	public CardType getType() {
		return type;
	}

	// Return whether this card is equal to another
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Card card = (Card) obj;
		return cardName.equals(card.cardName) && type == card.type;
	}

	// Returns a string respresentation of the card
	@Override
	public String toString() {
		return cardName;
	}	

	// Used for hashmap
	@Override
	public int hashCode() {
		return Objects.hash(cardName, type);
	}
}
