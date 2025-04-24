package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;


import clueGame.*;

public class GameSolutionTest {
	public static Board board;
	static Card person1, person2, person3, person4;
	static Card room1, room2, room3, room4;
	static Card weapon1, weapon2, weapon3, weapon4;

	@BeforeAll
	public static void setup(){
		board = Board.getInstance();
		board.setConfigFiles("/data/ClueLayout.csv", "/data/ClueSetup.txt");
		board.initialize();	
		// Use the same static cards for all tests
		person1 = new Card("Person1", CardType.PERSON);
		person2 = new Card("Person2", CardType.PERSON);
		person3 = new Card("Person3", CardType.PERSON);
		person4 = new Card("Person4", CardType.PERSON);
		
		room1 = new Card("room1", CardType.ROOM);
		room2 = new Card("room2", CardType.ROOM);
		room3 = new Card("room3", CardType.ROOM);
		room4 = new Card("room4", CardType.ROOM);

		weapon1 = new Card("weapon1", CardType.WEAPON);
		weapon2 = new Card("weapon2", CardType.WEAPON);
		weapon3 = new Card("weapon3", CardType.WEAPON);
		weapon4 = new Card("weapon4", CardType.WEAPON);

	}
	// Test a correct solution as well as solutions with one of each type wrong
	@Test
	public void testCheckAccusation() {
		Solution testSol = new Solution(person1, room1, weapon1);

		// Correct solution
		board.setSolution(new Solution(person1, room1, weapon1));
		assertTrue(board.checkAccusation(testSol));

		// Wrong person
		board.setSolution(new Solution(person2, room1, weapon1));
		assertFalse(board.checkAccusation(testSol));

		// Wrong room
		board.setSolution(new Solution(person1, room2, weapon1));
		assertFalse(board.checkAccusation(testSol));

		// Wrong weapon
		board.setSolution(new Solution(person1, room1, weapon2));
		assertFalse(board.checkAccusation(testSol));
	}

	// Test to make sure disproving a suggestion works
	@Test
	public void testDisproveSuggestion() {

		// Create player with a hand that we know
		Player player = new ComputerPlayer("TestPlayer", Color.white, 0, 0);
		player.updateHand(person1);
		player.updateHand(room1);
		player.updateHand(weapon1);

		// Test to make sure player will return the person card if it is the only one to disprove
		Card disproveCard = player.disproveSuggestion(new Solution(person1, room2, weapon2));
		assertNotNull(disproveCard);
		assertEquals(disproveCard, person1);

		// Test similar as above with room card
		disproveCard = player.disproveSuggestion(new Solution(person2, room1, weapon2));
		assertNotNull(disproveCard);
		assertEquals(disproveCard, room1);

		// Test similar as above with weapon card
		disproveCard = player.disproveSuggestion(new Solution(person2, room2, weapon1));
		assertNotNull(disproveCard);
		assertEquals(disproveCard, weapon1);
		
		// Make sure if player has no matching cards, it returns null
		disproveCard = player.disproveSuggestion(new Solution(person2, room2, weapon2));
		assertNull(disproveCard);
	}
	
	// Test and make sure handle suggestion works properly
	@Test
	public void testHandleSuggesion() {
		// Intialize players and hands
		Player humanPlayer1 = new HumanPlayer("humanPlayer1", Color.white, 0, 0);
		humanPlayer1.updateHand(person1);
		humanPlayer1.updateHand(room1);
		humanPlayer1.updateHand(weapon1);

		Player computerPlayer2 = new ComputerPlayer("computerPlayer2", Color.white, 0, 0);
		computerPlayer2.updateHand(person2);
		computerPlayer2.updateHand(room2);
		computerPlayer2.updateHand(weapon2);

		Player computerPlayer3 = new ComputerPlayer("computerPlayer3", Color.white, 0, 0);
		computerPlayer3.updateHand(person3);
		computerPlayer3.updateHand(room3);
		computerPlayer3.updateHand(weapon3);
		
		// Initialize players array in board to be accurate to our test
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(humanPlayer1);
		players.add(computerPlayer2);
		players.add(computerPlayer3);
		board.setPlayersArrayList(players);
		
		// Make sure a solution no one can disprove is null
		assertNull(board.handleSuggestion(new Solution(person4, room4, weapon4), humanPlayer1));
		
		// Make sure a solution only the asker can disprove is null
		assertNull(board.handleSuggestion(new Solution(person1, room1, weapon1), humanPlayer1));
		
		// If computer Player 3 suggests and human player 1 can disprove, ensure computer player 2 is not asked
		assertEquals(weapon1, board.handleSuggestion(new Solution(person2, room2, weapon1), computerPlayer3));
	}
}
