import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InputFilesGeneration {
    private static int currentID = 0;
    private static int totalContestants = 0;
    private static int totalFrauds = 0;

    /**
     * Generates a random int in [min, max]
     * @param min lower bound
     * @param max upper bound
     * @return res - int
     */
    private static int generateRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    private static List<Integer> generateContestants() {
        List<Integer> contestants = new ArrayList<>();
        int noContestants = generateRandomNumber(Constants.MIN_CONTESTANTS, Constants.MAX_CONTESTANTS);

        for (int i = 0; i < noContestants; i++) {
            contestants.add(currentID);
            currentID++;
        }

        return contestants;
    }

    private static List<Integer> generateScores(int currUnresolvedPbNo, int currFrauds, int contestantsNo) {
        int positiveScores = contestantsNo - currFrauds - currUnresolvedPbNo;

        List<Integer> scores = new ArrayList<>();
        for(int i=0;i<currFrauds;i++){
            scores.add(-1);
        }

        for(int i=0;i<positiveScores;i++){
            int score = generateRandomNumber(1, 100);
            scores.add(score);
        }

        Collections.shuffle(scores);
        return scores;
    }

    public static void main(String[] args) {
        for(int i = 1; i<= Constants.NO_COUNTRIES; i++) {
            System.out.println("Country C" + i + ":");

            List<Integer> contestants = generateContestants();
            int unresolvedProblemsNo = generateRandomNumber(0, (int) (0.1 * contestants.size()));
            int frauds = generateRandomNumber(0, (int) (0.02 * contestants.size()));

            totalContestants += contestants.size();
            totalFrauds += frauds;

            System.out.println("Contestants: " + contestants.size());
            System.out.println("Unresolved problems: " + unresolvedProblemsNo);
            System.out.println("Frauds: " + frauds);

            for(int j = 1; j<= Constants.NO_PROBLEMS; j++) {
                String filename = "RezultateC" + i + "_P" + j + ".txt";

                int currUnresolvedPbNo;
                int currFrauds;

                if(j == Constants.NO_PROBLEMS) {
                    currUnresolvedPbNo = unresolvedProblemsNo;
                    currFrauds = frauds;
                } else {
                    currUnresolvedPbNo = generateRandomNumber(0, unresolvedProblemsNo);
                    unresolvedProblemsNo -= currUnresolvedPbNo;

                    currFrauds = generateRandomNumber(0, frauds);
                    frauds -= currFrauds;
                }

                List<Integer> scores = generateScores(currUnresolvedPbNo, currFrauds, contestants.size());
                Collections.shuffle(contestants);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.PATH + filename))) {

                    for(int k=0;k<scores.size();k++) {
                        writer.write(contestants.get(k).toString() + ',' + scores.get(k).toString());
                        writer.newLine();
                    }

                    System.out.println("Wrote contents to file " + filename + " successfully!");
                } catch (IOException e) {
                    System.err.println("An error occurred while writing to the file: " + e.getMessage());
                }
            }

            System.out.println();
        }
        
        System.out.println("Total contestants: " + totalContestants);
        System.out.println("Total frauds: " + totalFrauds);
    }
}
