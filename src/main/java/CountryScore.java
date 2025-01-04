import java.io.Serializable;

public class CountryScore implements Serializable {

    private final String country;
    private int score;

    public CountryScore( String country, int score ) {
        this.country = country;
        this.score = score;
    }


    public void addScore( int score ) {
        this.score += score;
    }

    public int getScore( ) {
        return score;
    }

    @Override
    public String toString( ) {
        return "Country:" + country +
                ", Score:" + score;
    }
}
