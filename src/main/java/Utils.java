import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static List<ContestEntry> readEntries(File file, String countryName) {
        List<ContestEntry> contestEntries = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().strip();
                String[] elements = line.split(",");

                int contestantID = Integer.parseInt(elements[0]);
                int score = Integer.parseInt(elements[1]);

                contestEntries.add(new ContestEntry(contestantID, score, countryName));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return contestEntries;
    }
}
