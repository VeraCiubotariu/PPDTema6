import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logging {
    private static File logFile = new File(Constants.PATH + "Log.txt");

    // Clears the contents of the log file
    public static void clearLog() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false))) {
            // Open the file in "overwrite" mode and write nothing to clear it
            writer.write("");
        } catch (IOException e) {
            System.err.println("Failed to clear the log file: " + e.getMessage());
        }
    }

    // Appends a log message to the log file
    public static void log(String msg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(msg);
            writer.newLine(); // Add a newline after the message
        } catch (IOException e) {
            System.err.println("Failed to write to the log file: " + e.getMessage());
        }
    }
}
