package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class is used to create all human players habits (only 1)
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HumanPlayer extends Player {
	
	public HumanPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}

	// Selects matching cards at random if one or more exist
    @Override
    public Card disproveSuggestion(Solution suggestion) {
		
    	// Add all matching cards to matching array
        List<Card> matching = new ArrayList<>();
        for (Card card : getHand()) {
            if (card.equals(suggestion.getPerson()) || card.equals(suggestion.getWeapon()) || card.equals(suggestion.getRoom())) {
                matching.add(card);
            }
        }
        
        // Pick a random card if there exists a match, return null if not
        if (matching.isEmpty())
            return null;
            
        return matching.get(rand.nextInt(matching.size()));
    }
}
