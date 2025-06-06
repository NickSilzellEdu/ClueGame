package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class is used to create all the computer players habits
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import clueGame.Board;
public class ComputerPlayer extends Player{
	private boolean mysterySolved = false;

	public ComputerPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);

	}

	// Disprove a suggestion, kinda like it does in HumanPlayer.java
	@Override
	public Card disproveSuggestion(Solution suggestion) {

		// Add all matching cards to matching array
		List<Card> matching = new ArrayList<>();
		for (Card card : getHand()) {
			if (card.equals(suggestion.getPerson()) ||
					card.equals(suggestion.getWeapon()) ||
					card.equals(suggestion.getRoom())) {
				matching.add(card);
			}
		}

		// Pick a random card if there exists a match, return null if not
		if (matching.isEmpty())
			return null;

		return matching.get(rand.nextInt(matching.size()));
	}

	// Creates a suggestion
	public Solution createSuggestion() {
		Board board = Board.getInstance();
		BoardCell currentCell = board.getCell(this.getRow(), this.getCol());
		Room currentRoom = board.getRoom(currentCell);
		Card roomCard = null;

		// Find the room card from the deck that matches the current room name
		for (Card card : board.getDeck()) {
			if (card.getType() == CardType.ROOM && card.getCardName().equals(currentRoom.getName())) {
				roomCard = card;
				break;
			}
		}

		if (roomCard == null) {
			roomCard = new Card(currentRoom.getName(), CardType.ROOM);
		}

		List<Card> unseenPersons = new ArrayList<>();
		List<Card> unseenWeapons = new ArrayList<>();
		for (Card card : board.getDeck()) {
			if(!getHand().contains(card) && !getSeen().contains(card)) {
				if(card.getType() == CardType.PERSON) unseenPersons.add(card);
				else if(card.getType() == CardType.WEAPON) unseenWeapons.add(card);
			}
		}

		// For the person and weapon, choose a random unseen card/or not in hand
		Card personCard = null;
		Card weaponCard = null;

		if (!unseenPersons.isEmpty()) {
			personCard = unseenPersons.get(rand.nextInt(unseenPersons.size()));

		} else { // Choose randomly from all person cards
			List<Card> allPersons = new ArrayList<>();
			for (Card card : board.getDeck()) {
				if (card.getType() == CardType.PERSON) {
					allPersons.add(card);
				}
			}
			personCard = allPersons.get(rand.nextInt(allPersons.size()));
		}

		if (!unseenWeapons.isEmpty()) {
			weaponCard = unseenWeapons.get(rand.nextInt(unseenWeapons.size()));

		} else { // Choose randomly from all weapon cards
			List<Card> allWeapons = new ArrayList<>();
			for (Card card : board.getDeck()) {
				if (card.getType() == CardType.WEAPON) {
					allWeapons.add(card);
				}
			}
			weaponCard = allWeapons.get(rand.nextInt(allWeapons.size()));
		}    
		
		// If there is only one card of each type left, make the accusation
		if(this.getSeen().size() == Board.getInstance().getDeckSize() - 3 - this.getHand().size()) mysterySolved = true;
		
		return new Solution(roomCard, personCard, weaponCard);
	}

	// Selects a movement target from the given set of targets
	public BoardCell selectTarget(Set<BoardCell> targets) {
		List<BoardCell> roomTargets = new ArrayList<>();
		Board board = Board.getInstance();

		// If a room within roll distance is unseen, add it to room targets
		for (BoardCell cell : targets) {
			if (cell != null && cell.isRoom()) {
				Room room = board.getRoom(cell);
				Card roomCard = null;

				// Find the corresponding room card from the deck
				for (Card card : board.getDeck()) {
					if (card.getType() == CardType.ROOM && card.getCardName().equals(room.getName())) {
						roomCard = card;
						break;
					}
				}

				if (roomCard != null && !getSeen().contains(roomCard)) {
					roomTargets.add(cell);
				}
			}
		}

		// If the target room has not been seen, then pick a random target
		if (!roomTargets.isEmpty()) {
			return roomTargets.get(rand.nextInt(roomTargets.size()));
		}

		// A random target is selected if there is no rooms
		
		// If there is no targets, stay put
		if(targets.size() == 0) return board.getCell(getRow(), getCol());
		int index = rand.nextInt(targets.size());
		int i = 0;
		for (BoardCell cell : targets) {
			if (i == index)
				return cell;
			i++;
		}
		return null;
	}
	
	public boolean solvedMystery() {
		return mysterySolved;
	}
}
