package AST; // Declares the package name

import java.io.File; // Imports the File class for file handling
import java.io.IOException; // Imports the IOException class for handling IO exceptions
import java.util.ArrayList; // Imports the ArrayList class for dynamic array implementation
import java.util.List; // Imports the List interface for list operations

import lexical.LexicalAnalyzer; // Imports the LexicalAnalyzer class from the lexical package
import lexical.Token; // Imports the Token class from the lexical package
import Syntactic.Parser; // Imports the Parser class from the Syntactic package

public class ASTdriver { // Declares the ASTdriver class
    public static void main(String[] args) { // Main method, entry point of the program

        if (args.length < 1) { // Checks if no input file is provided
            System.out.println("Usage: java ASTdriver <input_file>"); // Prints usage message
            return; // Exits the program
        }
        String filePath = args[0]; // Gets the input file path from command line arguments

        try {
            File inputFile = new File(filePath); // Creates a File object for the input file
            if (!inputFile.exists()) { // Checks if the input file does not exist
                System.err.println("Error: File not found: " + filePath); // Prints error message
                return; // Exits the program
            }

            LexicalAnalyzer lexer = new LexicalAnalyzer(filePath); // Creates a LexicalAnalyzer object
            List<Token> tokenList = new ArrayList<>(); // Creates a list to store tokens

            Token token = lexer.getToken(); // Gets the first token from the lexer
            while (token != null) { // Loops until no more tokens are found
                tokenList.add(token); // Adds the token to the list
                token = lexer.getToken(); // Gets the next token from the lexer
            }
            lexer.close(); // Closes the lexer

            if (tokenList.isEmpty()) { // Checks if no tokens were found
                System.err.println("Error: No tokens found. Lexical analysis failed."); // Prints error message
                return; // Exits the program
            }

            Parser parser = new Parser(tokenList, filePath); // Creates a Parser object
            parser.parse(); // Parses the tokens

            ASTGenerator astGenerator = new ASTGenerator(); // Creates an ASTGenerator object
            String astInputFile = "output/" + inputFile + ".outderivation"; // Defines the output file path for derivation
            astGenerator.generateAST(astInputFile, "output/" + inputFile + ".outast"); // Generates the AST and writes to output files

            System.out.println("Parsing completed. Check output files for results."); // Prints completion message
        } catch (IOException e) { // Catches IO exceptions
            System.err.println("Error during parsing: " + e.getMessage()); // Prints error message
        }
    }
}
