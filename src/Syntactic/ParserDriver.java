package Syntactic;

import lexical.LexicalAnalyzer;
import lexical.Token;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserDriver {
    /**
     * The main driver function for the parser.
     * It takes an input file, performs lexical analysis to extract tokens,
     * and then passes the tokens to the parser for syntactic analysis.
     *
     * @param args Command-line arguments. The first argument should be the input file path.
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: java ParserDriver <input_file>");
            return;
        }
        String filePath = args[0];

        try {
            File inputFile = new File(filePath);
            if (!inputFile.exists()) {
                System.err.println("Error: File not found: " + filePath);
                return;
            }

            LexicalAnalyzer lexer = new LexicalAnalyzer(filePath);
            List<Token> tokenList = new ArrayList<>();

            Token token = lexer.getToken();
            while (token != null) {
                tokenList.add(token);
                token = lexer.getToken();
            }
            lexer.close();

//            System.out.println("DEBUG: Total Tokens Collected: " + tokenList.size());
//            for (Token t : tokenList) {
//                System.out.println("Token: " + t.getTokenType() + " -> " + t.getTokenValue());
//            }

            if (tokenList.isEmpty()) {
                System.err.println("Error: No tokens found. Lexical analysis failed.");
                return;
            }

            Parser parser = new Parser(tokenList, filePath);
            parser.parse();

            System.out.println("Parsing completed. Check output files for results.");
        } catch (IOException e) {
            System.err.println("Error during parsing: " + e.getMessage());
        }
    }
}
