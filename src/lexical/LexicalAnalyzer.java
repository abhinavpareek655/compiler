package lexical;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class LexicalAnalyzer {

    private final BufferedReader reader;
    private String currLine;
    private int currentCharIndex;
    private int lineNumber;

    public LexicalAnalyzer(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
        this.currLine = null;
        this.currentCharIndex = 0;
        this.lineNumber = 0;
    }
    public Token getToken() throws IOException {
        while (true) {
            while (currLine == null || currentCharIndex >= currLine.length()) {
                String nextLine = readUpcomingLine();
                if (nextLine == null) {
                    return null;
                }
                if (currLine.trim().isEmpty()) {
                    continue;
                }
            }
            while (currentCharIndex < currLine.length() &&
                    (currLine.charAt(currentCharIndex) == ' ' || currLine.charAt(currentCharIndex) == '\t')) {
                currentCharIndex++;
            }
            if (currentCharIndex >= currLine.length()) {
                continue;
            }
            return getNextToken();
        }
    }
    public Token getNextToken() throws IOException {
        StringBuilder tokenLexeme = new StringBuilder();
        Token token = null;
        char currChar = nextCharacter();

        while (currChar == ' ' || currChar == '\t') {
            currChar = nextCharacter();
        }

        if (Character.isLetter(currChar)) {
            tokenLexeme.append(currChar);
            currChar = nextCharacter();
            while (Character.isLetter(currChar) || Character.isDigit(currChar) || currChar == '_') {
                tokenLexeme.append(currChar);
                currChar = nextCharacter();
            }

            String lexeme = tokenLexeme.toString();
            if (checkKeyWord(lexeme)) {
                token = createToken(TokenType.valueOf(lexeme.toUpperCase()), lexeme);
            } else {
                token = createToken(TokenType.ID, lexeme);
            }
            if (currChar != '\uFFFF') lastChar();
        }

        else if (Character.isDigit(currChar)) {
            boolean isFloat = false;
            boolean containDot = false;
            boolean isZero = false;
            boolean isValid = true;

            if (currChar == '0') isZero = true;
            tokenLexeme.append(currChar);

            currChar = nextCharacter();

            while (currLine.length() > currentCharIndex || (Character.isDigit(currChar) || currChar == '.')) {
                if (Character.isDigit(currChar)) {
                    tokenLexeme.append(currChar);
                    currChar = nextCharacter();
                }

                else if (currChar == '.') {
                    if (!containDot) {
                        containDot = true;
                        tokenLexeme.append(currChar);
                        currChar = nextCharacter();

                        if (!Character.isDigit(currChar)) {
                            return createToken(TokenType.LEXICAL_ERR, "Invalid float number format: " + tokenLexeme.toString());
                        }
                        isFloat = true;
                    } else {

                        break;
                    }
                } else {

                    break;
                }
            }

            if (currChar == 'e') {
                tokenLexeme.append(currChar);
                currChar = nextCharacter();

                if (currChar == '+' || currChar == '-') {
                    tokenLexeme.append(currChar);
                    currChar = nextCharacter();
                }
                isZero = currChar == '0';
                if (Character.isDigit(currChar)) {
                    do {
                        tokenLexeme.append(currChar);
                        currChar = nextCharacter();
                        if(currChar!='0' && isZero) {
                            isValid = false;
                        }
                    } while (Character.isDigit(currChar));

                    String integerValue = tokenLexeme.substring(0, tokenLexeme.indexOf("."));
                    String fractionValue = tokenLexeme.substring(tokenLexeme.indexOf(".")+1, tokenLexeme.indexOf("e"));
                    if((integerValue.charAt(0) == '0' && integerValue.length()>1) || fractionValue.length()>1 && fractionValue.charAt(fractionValue.length()-1)=='0') {
                        return createToken(TokenType.LEXICAL_ERR, "Invalid scientific notation: " + tokenLexeme.toString());
                    }
                    if (!isValid){
                        return createToken(TokenType.LEXICAL_ERR, "Invalid scientific notation: " + tokenLexeme.toString());
                    }
                    token = createToken(TokenType.FLOATLIT, tokenLexeme.toString());
                } else {
                    return createToken(TokenType.LEXICAL_ERR, "Invalid scientific notation: " + tokenLexeme.toString());
                }
            } else {
                if (isFloat) {
                    String integerValue = tokenLexeme.substring(0, tokenLexeme.indexOf("."));
                    String fractionValue = tokenLexeme.substring(tokenLexeme.indexOf(".")+1, tokenLexeme.length());
                    if((integerValue.charAt(0) == '0' && integerValue.length()>1) || fractionValue.length()>1 && fractionValue.charAt(fractionValue.length()-1)=='0') {
                        return createToken(TokenType.LEXICAL_ERR, "Invalid number with leading zero: " + tokenLexeme.toString());
                    }
                    token = createToken(TokenType.FLOATLIT, tokenLexeme.toString());
                } else {
                    if (isZero && tokenLexeme.length() > 1) {
                        token = createToken(TokenType.LEXICAL_ERR, "Invalid number with leading zero: " + tokenLexeme.toString());
                    } else {
                        token = createToken(TokenType.INTLIT, tokenLexeme.toString());
                    }
                }
            }

            if (currChar != '\uFFFF') lastChar();
        }
        else {
            switch (currChar) {
                case '=':
                    currChar = nextCharacter();
                    if (currChar == '=') {
                        token = createToken(TokenType.EQ, "==");
                    } 
                    else if (currChar == '>') {
                        token = createToken(TokenType.ARROW, "=>");
                    }
                    break;
                case '>':
                    currChar = nextCharacter();
                    if (currChar == '=') {
                        token = createToken(TokenType.GEQ, ">=");
                    } else {
                        token = createToken(TokenType.GT, ">");
                    }
                    break;
                case '<':
                    currChar = nextCharacter();
                    if (currChar == '=') {
                        token = createToken(TokenType.LEQ, "<=");
                    } else if (currChar == '>') {
                        token = createToken(TokenType.NEQ, "<>");
                    } else {
                        token = createToken(TokenType.LT, "<");
                    }
                    break;
                case '/':
                    currChar = nextCharacter();
                    currentCharIndex++;
                    if (currChar == '/') {
                        token = new Token(TokenType.COMMENT, currLine.substring(currentCharIndex-3), lineNumber);
                        currLine = reader.readLine();
                        lineNumber++;
                        currentCharIndex = 0;
                    }
                    else if (currChar != '*') {
                        token = createToken(TokenType.DIVISION, "/");
                    }
                    else {
                        StringBuilder lexeme = new StringBuilder("/*");
                        while (true) {
                            if(currentCharIndex >= currLine.length()) {
                                currLine = reader.readLine();
                                if(currLine ==null) {
                                    return createToken(TokenType.LEXICAL_ERR, "unclosed multi-line comment");
                                }
                                lineNumber++;
                                currentCharIndex = 0;
                                lexeme.append("\\n");
                                continue;
                            }

                            currChar = currLine.charAt(currentCharIndex);
                            lexeme.append(currChar);
                            currentCharIndex++;
                            if (currChar == '*') {
                                if (currentCharIndex< currLine.length() && currLine.charAt(currentCharIndex) == '/') {
                                    currChar = nextCharacter();
                                    lexeme.append(currChar);
                                    currentCharIndex++;
                                    return createToken(TokenType.MULTI_LINE_COMMENT, lexeme.toString());
                                }
                            }
                        }
                    }
                    break;
                case '*':
                    token = createToken(TokenType.MULTIPLICATION, "*");
                    break;
                case '+':
                    token = createToken(TokenType.ADDITION, "+");
                    break;
                case '-':
                    token = createToken(TokenType.SUBTRACTION, "-");
                    break;
                case '(':
                    token = createToken(TokenType.LPAREN, "(");
                    break;
                case ')':
                    token = createToken(TokenType.RPAREN, ")");
                    break;
                case '{':
                    token = createToken(TokenType.LBRACE, "{");
                    break;
                case '}':
                    token = createToken(TokenType.RBRACE, "}");
                    break;
                case '[':
                    token = createToken(TokenType.LSQBR, "[");
                    break;
                case ']':
                    token = createToken(TokenType.RSQBR, "]");
                    break;
                case ';':
                    token = createToken(TokenType.SEMICOLON, ";");
                    break;
                case ':':
                    currChar = nextCharacter();
                    if (currChar == '=') {
                        token = createToken(TokenType.ASSIGN, ":=");
                    } 
                    else {
                        token = createToken(TokenType.COLON, ":");
                    }
                    break;
                case '.':
                    token = createToken(TokenType.DOT, ".");
                    break;
                case ',':
                    token = createToken(TokenType.COMMA, ",");
                    break;
                case '\uFFFF':
                    break;
                default:
                    token = createToken(TokenType.LEXICAL_ERR, "Unknown lex " + String.valueOf(currChar));
            }
        }
        return token;
    }

    private char nextCharacter() {
        if (currentCharIndex < currLine.length()) {
            return currLine.charAt(currentCharIndex++);
        } else {
            return '\uFFFF';
        }
    }

    private void lastChar() {
        if (currentCharIndex > 0) currentCharIndex--;
    }

    private String readUpcomingLine() throws IOException {
        String line = reader.readLine();
        if (line != null) {
            currLine = line;
            currentCharIndex = 0;
            lineNumber++;
        }
        return line;
    }

    private boolean checkKeyWord(String lexeme) {
        return Arrays.asList("if", "then", "else", "class", "return", "int", "float", "while", "and", "or", "not","void", "self", "isa", "implementation", "read", "write", "local", "constructor", "attribute", "function", "public", "private").contains(lexeme);
    }

    private Token createToken(TokenType type, String lexeme) {
        return new Token(type, lexeme, lineNumber);
    }

    public void close() throws IOException {
        reader.close();
    }
}
