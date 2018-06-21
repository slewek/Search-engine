import java.math.BigDecimal;
import java.util.ArrayList;

public class Document {
    private int number;
    private ArrayList<BigDecimal> tf_idf;

    public Document(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<BigDecimal> getTf_idf() {
        return tf_idf;
    }

    public void setTf_idf(ArrayList<BigDecimal> tf_idf) {
        this.tf_idf = tf_idf;
    }
}
