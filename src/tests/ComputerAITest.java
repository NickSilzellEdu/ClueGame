package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.*;


// This class tests the functionality of the AI players
public class ComputerAITest {
	private static Board board;
	private static ComputerPlayer computerPlayer;

	// Setup
	@BeforeAll
	public static void setup() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();

		// Initialize a computer player from player's list
		for(Player player : board.getPlayers()) {
			if(player instanceof ComputerPlayer) {
				computerPlayer = (ComputerPlayer)player;
				break;
			}
		}
		assertNotNull(computerPlayer);
	}

	// Test computer's createSuggestion function
	@Test
	public void testCreateSuggestion() {
		
		// Empty player's hands because board deals cards automatically
		computerPlayer.getHand().clear();

		// Make sure a player's suggestion reflects the room they are in
		computerPlayer.setRow(11);
		computerPlayer.setCol(5);
		Card testCard = new Card("Army Camp", CardType.ROOM);// Identical to the deck's army camp card
		Solution testSolution = computerPlayer.createSuggestion();
		assertEquals(testCard, testSolution.getRoom());

		// Add all cards in the deck besides one weapon card and one person to the player's seen cards
		for(Card card : board.getDeck()) {
			computerPlayer.addSeen(card);
		}

		// Remove and store a weapon card
		Card removedWeaponCard = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.WEAPON) {
				removedWeaponCard = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}
		// Remove and store a person card
		Card removedPersonCard = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.PERSON) {
				removedPersonCard = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}

		// Make sure both cards are selected if they are the only ones remaining
		testSolution = computerPlayer.createSuggestion();
		assertNotNull(removedWeaponCard);
		assertNotNull(removedPersonCard);
		assertEquals(removedWeaponCard, testSolution.getWeapon());
		assertEquals(removedPersonCard, testSolution.getPerson());

		// Remove two more weapon cards and person cards, and make sure the suggestion contains one of them
		Card removedWeaponCard2 = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.WEAPON) {
				removedWeaponCard2 = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}
		Card removedPersonCard2 = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.PERSON) {
				removedPersonCard2 = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}
		Card removedWeaponCard3 = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.WEAPON) {
				removedWeaponCard3 = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}
		Card removedPersonCard3 = null;
		for(Card card : board.getDeck()) {
			if(card.getType() == CardType.PERSON) {
				removedPersonCard3 = card;
				computerPlayer.getSeen().remove(card);
				break;
			}
		}
		
		ArrayList<Card> unseenWeapons = new ArrayList<Card>();
		unseenWeapons.add(removedWeaponCard);
		unseenWeapons.add(removedWeaponCard2);
		unseenWeapons.add(removedWeaponCard3);
		
		ArrayList<Card> unseenPersons = new ArrayList<Card>();
		unseenPersons.add(removedPersonCard);
		unseenPersons.add(removedPersonCard2);
		unseenPersons.add(removedPersonCard3);

		// Make sure suggestion contains one of the three cards for each type
		testSolution = computerPlayer.createSuggestion();
		assertTrue(unseenWeapons.contains(testSolution.getWeapon()));
		assertTrue(unseenPersons.contains(testSolution.getPerson()));
	}
	
	// Test computer's selectTargetUnseen function
	@Test
	public void testSelectTargetUnseen() {
		Set<BoardCell> targets = new HashSet<>();
		BoardCell roomCell = board.getCell(5, 9);
		targets.add(roomCell);
		targets.add(board.getCell(10, 10));
		targets.add(board.getCell(10, 11));

		computerPlayer.getSeen().clear();
		BoardCell selected = computerPlayer.selectTarget(targets);
		assertEquals(roomCell, selected);
	}

	// Test computer's selectTargetNoUnseen function
	@Test
	public void testSelectTargetNoUnseen() {
		Set<BoardCell> targets = new HashSet<>();
		BoardCell cell1 = board.getCell(10, 10);
		BoardCell cell2 = board.getCell(10, 11);
		targets.add(cell1);
		targets.add(cell2);

		computerPlayer.getSeen().clear();
		for (int i = 0; i < 10; i++) {
			BoardCell selected = computerPlayer.selectTarget(targets);
			assertTrue(targets.contains(selected));
		}
	}
}
