package clueGame;

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
	public boolean equals(Card target) {
//		if (target == null) return false;
//		return this.cardName.equals(target.cardName) && this.type == target.type;
		return false;
	}
	
	// Returns a string respresentation of the card
	@Override
	public String toString() {
		return cardName + " (" + type + ")";
	}	
}
