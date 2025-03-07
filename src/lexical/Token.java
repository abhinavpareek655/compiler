package lexical;

public class Token {

    private TokenType tokenType;
    private String tokenValue;
    private int tokenPosition;

    public Token(TokenType tokenType, String tokenValue, int tokenPosition) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
        this.tokenPosition = tokenPosition;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public int getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s, %d>", tokenType, tokenValue, tokenPosition);
    }
}
