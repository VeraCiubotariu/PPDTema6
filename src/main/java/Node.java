import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private final int contestantID;
    private int score;
    private final String countryName;
    private Node next;
    private final Lock lock;

    public Node(int contestantID, int score, String countryName) {
        this.contestantID = contestantID;
        this.score = score;
        this.countryName = countryName;
        this.next = null;
        this.lock = new ReentrantLock();
    }

    public int getContestantID() {
        return contestantID;
    }

    public int getScore() {
        return score;
    }

    public Node getNext() {
        return next;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Lock getLock() {
        return lock;
    }

    public void addScore(int score) {
        this.score += score;
    }
}
