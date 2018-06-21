import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Score implements Comparable<Score> {
    private int number;
    private double similarity;

    public Score(int number, double similarity) {
        this.number = number;
        this.similarity = similarity;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    @Override
    public int compareTo(Score o)
    {
        if (this.similarity > o.similarity) return -1;
        else if (this.similarity < o.similarity) return 1;
        return 0;
     //   return this.similarity.compareTo(o.similarity);
    }
}
