package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HumanPlayer extends Player {
	
	public HumanPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}
	
	@Override
	public void updateHand(Card card) {
		getHand().add(card);
	}
	
	// Selects matching cards at random if one or more exist
    @Override
    public Card disproveSuggestion(Solution suggestion) {
        List<Card> matching = new ArrayList<>();
        for (Card card : getHand()) {
            if (card.equals(suggestion.getPerson()) ||
                card.equals(suggestion.getWeapon()) ||
                card.equals(suggestion.getRoom())) {
                matching.add(card);
            }
        }
        
        if (matching.isEmpty())
            return null;
        Random rand = new Random();
        return matching.get(rand.nextInt(matching.size()));
    }
}
