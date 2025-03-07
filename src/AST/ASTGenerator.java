package AST; // Define the package for this class

import java.io.*; // Import Java IO classes
import java.util.*; // Import Java utility classes
import java.nio.file.Files; // Import Files class for file operations
import Syntactic.*;

// Define a class to represent a node in the AST
class ASTNode {
    String value; // The value of the node
    List<ASTNode> children; // The children of the node

    // Constructor to initialize the node with a value
    ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>(); // Initialize the children list
    }

    // Method to add a child to the node
    void addChild(ASTNode child) {
        children.add(child);
    }
}

// Define the main class for generating the AST
public class ASTGenerator {
    // Method to generate the AST from an input file and write it to an output file
    public void generateAST(String inputFile, String outputFile) {
        try {
            // Read all lines from the input file
            List<String> lines = Files.readAllLines(new File(inputFile).toPath());
            // Build the AST from the lines
            ASTNode root = buildAST(lines);
            // Write the AST to the output file
            writeASTToFile(root, outputFile);
            // Print a success message
            System.out.println("AST written to: " + outputFile);
        } catch (IOException e) {
            // Print an error message if an exception occurs
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Method to build the AST from a list of lines
    private ASTNode buildAST(List<String> lines) {
        Stack<ASTNode> stack = new Stack<>(); // Stack to keep track of nodes
        ASTNode root = null; // Root of the AST

        // Iterate over each line
        for (String line : lines) {
            // Check if the line starts with "=> "
            if (line.startsWith("=> ")) {
                String content = line.substring(3).trim(); // Extract the content after "=> "
                // Check if the content starts with "Matched terminal: "
                if (content.startsWith("Matched terminal: ")) {
                    String terminal = content.replace("Matched terminal: ", "").trim(); // Extract the terminal
                    // If the stack is not empty, add the terminal as a child to the top node
                    if (!stack.isEmpty()) {
                        ASTNode parent = stack.peek();
                        parent.addChild(new ASTNode(terminal));
                    }
                } else if (content.contains("->")) { // Check if the content contains "->"
                    String[] parts = content.split(" -> "); // Split the content into LHS and RHS
                    String lhs = parts[0].trim(); // Extract the LHS
                    String[] rhs = parts[1].split("\\s+"); // Extract the RHS

                    ASTNode parentNode;
                    // If the stack is empty and root is null, create the root node
                    if (stack.isEmpty() && root == null) {
                        parentNode = new ASTNode(lhs);
                        root = parentNode;
                        stack.push(parentNode);
                    } else {
                        // If the stack is empty but root is not null, print an error message
                        if (stack.isEmpty()) {
                            System.err.println("Error: Stack is empty but expected to process " + lhs);
                            continue;
                        }
                        parentNode = stack.pop(); // Pop the top node from the stack
                        // Check if the popped node's value matches the LHS
                        if (!parentNode.value.equals(lhs)) {
                            System.err.println("Mismatch: Expected " + parentNode.value + " but got " + lhs);
                            stack.push(parentNode); // Push the node back to the stack if it doesn't match
                            continue;
                        }
                    }

                    List<ASTNode> children = new ArrayList<>(); // List to store the children nodes
                    // Iterate over each child name in the RHS
                    for (String childName : rhs) {
                        childName = childName.trim(); // Trim the child name
                        // If the child name is not empty, create a new node and add it to the children list
                        if (!childName.isEmpty()) {
                            children.add(new ASTNode(childName));
                        }
                    }

                    // Add each child to the parent node
                    for (ASTNode child : children) {
                        parentNode.addChild(child);
                    }

                    // Push each child to the stack in reverse order
                    for (int i = children.size() - 1; i >= 0; i--) {
                        if(!Parser.isTerminal(children.get(i).value)) {
                            stack.push(children.get(i));
                        }
                    }
                }
            }
        }

        return root; // Return the root of the AST
    }

    // Method to write the AST to a file
    private static void writeASTToFile(ASTNode node, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writeNode(writer, node, ""); // Write the root node to the file
        }
    }

    // Recursive method to write a node and its children to the file
    private static void writeNode(BufferedWriter writer, ASTNode node, String indent) throws IOException {
        writer.write(indent + node.value); // Write the node value with the current indentation
        writer.newLine(); // Write a new line
        // Iterate over each child and write it to the file with increased indentation
        for (ASTNode child : node.children) {
            writeNode(writer, child, indent + "  ");
        }
    }
}