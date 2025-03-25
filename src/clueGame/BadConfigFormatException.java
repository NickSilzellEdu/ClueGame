package clueGame;

/*
 * Created by Nick Silzell and Andrew Grimes
 * This class is designed for exceptions specific to bad data in setup or layout files
 */
 
// Custom exception for configuration file format errors
public class BadConfigFormatException extends Exception{
	
	// Quick Fix BadConfigFormatException warning, not sure what this does
    private static final long serialVersionUID = 1L;

	// Constructor accepting a custom error message
    public BadConfigFormatException(String message) {
        super(message);
    }
    
    // Default constructor with a preset error message
    public BadConfigFormatException() {
        super("Error: bad format in setup or layout file");
    }
    
    // Returns the exception's message
    @Override
    public String toString() {
        return this.getMessage();
    }
}
