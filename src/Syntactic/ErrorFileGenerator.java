package Syntactic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorFileGenerator {
    /**
     * Writes error messages to an output file if there are any errors.
     *
     * @param fileName  The name of the output file (without extension).
     * @param errors    The error messages to be written.
     * @param errorType The type of error (e.g., "Lexical", "Syntax").
     * @throws IOException If an error occurs while writing to the file.
     */
    public static void outputError(String fileName, String errors, String errorType) throws IOException {
        if (errors.isEmpty()) return;

        String errorFilePath = "output/" + fileName + ".out" + errorType.toLowerCase() + "errors";
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(errorFilePath)));
        bw.write("========= " + errorType + " Errors =========\n");
        bw.write(errors);
        bw.write("========= End of Errors =========\n");
        bw.close();
    }
}
