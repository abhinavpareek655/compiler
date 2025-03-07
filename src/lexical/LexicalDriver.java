package lexical;

import Syntactic.Parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LexicalDriver {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Correct usage: java LexicalDriver");
            return;
        }
        String inputSourceFile = args[0];

        File inputFile = new File(inputSourceFile);
        if (!inputFile.exists()) {
            System.err.println("Error: Input file not found: " + inputSourceFile);
            return;
        }
        String tokensFilePath = inputSourceFile.replace(".src", ".outlextokens");
        String errorsFilePath = inputSourceFile.replace(".src", ".outlexerrors");

        try (BufferedWriter tokensWriter = Files.newBufferedWriter(Paths.get(tokensFilePath));
             BufferedWriter errorsWriter = Files.newBufferedWriter(Paths.get(errorsFilePath))) {

            LexicalAnalyzer lexer = new LexicalAnalyzer(inputSourceFile);
            List<Token> tokensList = new ArrayList<>();

            Token token = lexer.getToken();
            while (token != null) {
                if(token.getTokenType() == TokenType.LEXICAL_ERR){
                    errorsWriter.write(token.toString());
                    errorsWriter.newLine();
                }
                tokensWriter.write(token.toString());
                tokensWriter.newLine();
                tokensList.add(token);
                token = lexer.getToken();
            }

            System.out.println("Tokens written to: " + tokensFilePath);
            System.out.println("Errors written to: " + errorsFilePath);
            lexer.close();

            // Pass stored tokens to Parser
            System.out.println("\nStarting Syntactic Analysis...");
            Parser parser = new Parser(tokensList, inputSourceFile);  // Pass tokens list
            parser.parse();

        } catch (IOException e) {
            System.err.println("Error handling files: " + e.getMessage());
        }
    }
}
