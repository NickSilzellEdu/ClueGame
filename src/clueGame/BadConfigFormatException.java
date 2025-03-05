package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class is designed for exceptions specific to bad data in setup or layout files
 */
public class BadConfigFormatException extends Exception{

	public BadConfigFormatException(String message) {
		super(message);
	}
	
	public BadConfigFormatException() {
		super("Error: bad format in setup or layout file");
	}
	
	@Override
	public String toString() {
		return this.getMessage();	}
	
}
