package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player{
	
	public ComputerPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}
	
	@Override
	public void updateHand(Card card) {
		getHand().add(card);
	}
	
	// Disprove a suggestion, kinda like it does in HumanPlayer.java
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
            if (card.getType() == CardType.PERSON) {
                if (!getHand().contains(card) && !getSeen().contains(card)) {
                    unseenPersons.add(card);
                }
            } else if (card.getType() == CardType.WEAPON) {
                if (!getHand().contains(card) && !getSeen().contains(card)) {
                    unseenWeapons.add(card);
                }
            }
        }
        
        // For the person and weapon, choose a random unseen card/or not in hand
        Random rand = new Random();
        Card personCard;
        Card weaponCard;
        if (unseenPersons.size() > 0) {
            personCard = unseenPersons.get(rand.nextInt(unseenPersons.size()));
        } else {
            // Choose randomly from all person cards
            List<Card> allPersons = new ArrayList<>();
            for (Card card : board.getDeck()) {
                if (card.getType() == CardType.PERSON) {
                    allPersons.add(card);
                }
            }
            personCard = allPersons.get(rand.nextInt(allPersons.size()));
        }
        
        if (unseenWeapons.size() > 0) {
            weaponCard = unseenWeapons.get(rand.nextInt(unseenWeapons.size()));
        } else {
            // Choose randomly from all weapon cards
            List<Card> allWeapons = new ArrayList<>();
            for (Card card : board.getDeck()) {
                if (card.getType() == CardType.WEAPON) {
                    allWeapons.add(card);
                }
            }
            weaponCard = allWeapons.get(rand.nextInt(allWeapons.size()));
        }
        
        return new Solution(roomCard, personCard, weaponCard);
    }
    
    // Selects a movement target from the given set of targets
    public BoardCell selectTarget(Set<BoardCell> targets) {
        List<BoardCell> roomTargets = new ArrayList<>();
        Board board = Board.getInstance();
        for (BoardCell cell : targets) {
            if (cell.isRoom()) {
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
        
        // If the target room has not been seen, then the room target is randomly chosen
        Random rand = new Random();
        if (!roomTargets.isEmpty()) {
            return roomTargets.get(rand.nextInt(roomTargets.size()));
        }
        
        // A random target is selected
        int index = rand.nextInt(targets.size());
        int i = 0;
        for (BoardCell cell : targets) {
            if (i == index)
                return cell;
            i++;
        }
        
        return null; 
    }
}
