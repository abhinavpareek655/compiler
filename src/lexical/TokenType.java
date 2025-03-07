package lexical;

public enum TokenType {

    IF, THEN, ELSE, CLASS, RETURN, INT, FLOAT, WHILE,
    AND, OR, NOT,
    INTLIT, FLOATLIT,
    ID,
    COMMENT,
    EQ, NEQ,
    GEQ, LEQ, GT, LT,
    ASSIGN,
    SEMICOLON, DOT, COMMA,COLON,
    ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION,
    LPAREN, RPAREN, LBRACE, RBRACE, LSQBR, RSQBR,
    VOID, SELF, ISA, IMPLEMENTATION, READ, WRITE, LOCAL, CONSTRUCTOR, ATTRIBUTE, FUNCTION, PUBLIC, PRIVATE,
    MULTI_LINE_COMMENT, LEXICAL_ERR, EOF,

    // New tokens added to accommodate syntactic requirements
    STRING_LIT,  // For string literals
    ASSIGN_OP,   // For assignment operator
    REL_OP,      // For relational operator
    ARROW,       // New token for arrow (e.g., for lambda functions or some specific syntax)
}
