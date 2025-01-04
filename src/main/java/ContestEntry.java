import java.io.Serializable;

public record ContestEntry(int contestantID, int score, String country) implements Serializable {
    @Override
    public String toString() {
        return "ContestEntry{" +
                "contestantID=" + contestantID +
                ", score=" + score +
                ", country='" + country + '\'' +
                '}';
    }
}
