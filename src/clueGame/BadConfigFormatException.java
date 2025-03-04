package clueGame;


public class BadConfigFormatException extends Exception{

	public BadConfigFormatException(String message) {
		super(message);
	}
	
	public BadConfigFormatException() {
		super("Error: bad format");
	}
	
	@Override
	public String toString() {
		return this.getMessage();	}
	
}
