package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import clueGame.*;

/*
 * Created by Nick Silzell and Andrew Grimes
 * Designed to test functionality of player class as well as loading in players and weapons
 */

public class GameSetupTests {
	private static Board board;

	// Make sure board, player, and deck are initialized
	@BeforeAll
	public static void setup() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueLayout.csv", "data/ClueSetup.txt");
		board.initialize();	
	}

	// Test to ensure there are a total of six players
	@Test
	public void testLoadPLayers() {
		assertEquals(Board.NUM_PLAYERS, board.getPlayers().size());
		Player p1 = board.getPlayers().get(0);
		assertTrue(p1 instanceof HumanPlayer);

		for(int i = 1; i < Board.NUM_PLAYERS; i++) {
			assertTrue(board.getPlayers().get(i) instanceof ComputerPlayer);
		}
	}

	// Test to ensure the answer is a valid solution to the game
	@Test
	public void testSolution() {
		Card testPerson = new Card("Jim", CardType.PERSON);
		Card testRoom = new Card("Hallway", CardType.ROOM);
		Card testWeapon = new Card("Gun", CardType.WEAPON);
		Solution sol = new Solution(testRoom, testPerson, testWeapon);

		assertNotNull(sol);
		assertTrue(sol.getPerson().equals(testPerson));
		assertTrue(sol.getRoom().equals(testRoom));
		assertTrue(sol.getWeapon().equals(testWeapon));

	}

	// Test to ensure no two players have the same card, and that players have roughly the same number of cards
	@Test
	public void testPlayerDeal() {
		// Make sure all player's hands are empty
		for(Player player : board.getPlayers()) {
			player.getHand().clear();
		}
		
		HashSet<Card> dealtCards = new HashSet<Card>();
		TreeSet<Integer> handSizes = new TreeSet<Integer>();
		board.deal();
		for(Player player : board.getPlayers()) {
			// Add each hand size to a sorted Set
			handSizes.add((player.getHand().size()));

			// Make sure no two players have the same card
			for(Card card : player.getHand()) {
				assertFalse(dealtCards.contains(card));
				dealtCards.add(card);
			}
		}
		assertFalse(dealtCards.isEmpty());
		// Make sure hand sizes don't differ by more than one
		assertTrue(Math.abs(handSizes.first()-handSizes.last()) <= 1 );		

	}

	// Test to ensure the whole map is loaded (9 rooms, 6 people, 6 weapons)
	@Test
	public void testLoadDeck() {
		ArrayList<Card> deck = board.getDeck();
		assertEquals(Board.DECK_SIZE, deck.size());

		// Count each card type
		int roomCount = 0;
		int personCount = 0;
		int weaponCount = 0;

		// Count the cards by type
		for (Card card : deck) {
			if (card.getType() == CardType.ROOM) {
				roomCount++;
			} else if (card.getType() == CardType.PERSON) {
				personCount++;
			} else if (card.getType() == CardType.WEAPON) {
				weaponCount++;
			}
		}

		// Assertions for exact amount of rooms, people, and weapons
		assertEquals(Board.NUM_ROOMS, roomCount);
		assertEquals(Board.NUM_PLAYERS, personCount);
		assertEquals(Board.NUM_WEAPONS, weaponCount);
	}
	
	// Make sure solution is random every round
	@Test
	public void testRandomSolution() {
		Solution sol;
		HashMap<Card, Integer> instances = new HashMap<Card, Integer>();
		int personCount = 0;
		int roomCount = 0;
		int weaponCount = 0;
		// Create 500 solutions, increment instances
		for(int i = 0; i < 500; i++) {
			board.getRandomSolution();
			sol = board.getSolution();
			// Increment count of each card by 1
			instances.put(sol.getRoom(), instances.getOrDefault(sol.getRoom(), 0) + 1); 
			instances.put(sol.getPerson(), instances.getOrDefault(sol.getPerson(), 0) + 1); 
			instances.put(sol.getWeapon(), instances.getOrDefault(sol.getWeapon(), 0) + 1); 
			
		}
		// Make sure each card has been picked at least 10 times
		for(Card card : board.getDeck()) {
			assertTrue(instances.get(card) >= 10);
		}
		
		// Make sure room cards, player cards, and weapon cards are equal
		for(Card card : instances.keySet()) {
			if (card.getType() == CardType.ROOM) {
				roomCount += instances.get(card);
			} else if (card.getType() == CardType.PERSON) {
				personCount += instances.get(card);
			} else if (card.getType() == CardType.WEAPON) {
				weaponCount += instances.get(card);
			}
		}
		assertTrue(personCount == roomCount);
		assertTrue(roomCount == weaponCount);
	}
}




