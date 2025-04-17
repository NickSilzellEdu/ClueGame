package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
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
		return room;
	}
	
	public Card getPerson() {
		return person;
	}
	
	public Card getWeapon() {
		return weapon;
	}
}
