package clueGame;
/*
 * Designed by Nick Silzell and Andrew Grimes
 * this class is used to store the game solution, which varies between games
 */
public class Solution {
	private Card room;
	private Card person;
	private Card weapon;
	
	public Solution(Card room, Card person, Card weapon) {
		this.room = room;
		this.person = person;
		this.weapon = weapon;
	}
	public Card getRoom() {
		return new Card("a", CardType.PERSON); // Stub
	}
	public Card getPerson() {
		return new Card("a", CardType.PERSON); // Stub
	}
	public Card getWeapon() {
		return new Card("a", CardType.PERSON); // Stub
	}
	
}
