package net.homeunix.siam.wordcounter;

public class TokenAndType {
	public enum TokenType {WORD, DELIMITER, UNKNOWN};
	
	public String token;
	public TokenType type;
	
	TokenAndType(String token, TokenType type) {
		this.token = token;
		this.type = type;
	}
	
	public String toString() {
		return this.token;
	}
}