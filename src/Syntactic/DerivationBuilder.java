package Syntactic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class DerivationBuilder {
    private LinkedList<String> derivation = new LinkedList<>();
    private BufferedWriter bw;

    /**
     * Initializes the derivation builder, creates the output directory if necessary,
     * and opens a file for writing the derivation steps.
     *
     * @param fileName The name of the output file (without extension).
     * @throws IOException If an error occurs while creating the file.
     */
    public DerivationBuilder(String fileName) throws IOException {
        String outputPath = "output/" + fileName + ".outderivation";
        File outputDir = new File("output/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        this.bw = new BufferedWriter(new FileWriter(new File(outputPath)));
        this.bw.write("========= Derivation begin =========\n");
    }

    /**
     * Stores an initial token value in the derivation list.
     *
     * @param tokenValue The token value to be added to the derivation.
     */
    public void set(String tokenValue) {
        this.derivation.add(tokenValue);
    }

    /**
     * Appends a derivation step to the output file.
     *
     * @param step The derivation step to be recorded.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void appendStep(String step) throws IOException {
        this.bw.write("=> " + step + "\n");
    }

    /**
     * Closes the derivation output file after writing the final derivation end marker.
     *
     * @throws IOException If an error occurs while closing the file.
     */
    public void close() throws IOException {
        this.bw.write("========= Derivation end =========\n");
        this.bw.close();
    }
}
