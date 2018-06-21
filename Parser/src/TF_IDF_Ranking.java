import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TF_IDF_Ranking {
    public double[] tf(List<String> doc, List<String> terms) {
        double[] tf = new double[terms.size()];
        for (String word : doc) {
            if (terms.contains(word)) {
                int idx = terms.indexOf(word);
                tf[idx]++;
            }
        }
        return tf;
    }

    public ArrayList<Double> idf(List<List<String>> documents, List<String> keywords) {
        ArrayList<Double> idf = new ArrayList<>(keywords.size());
        for (String term : keywords) {
            int documentValue = 0;
            for (List<String> document : documents) {
                if (document.contains(term)) {
                    documentValue++;
                }
            }
            if (documentValue == 0) {
                idf.add(0.0);
            } else {
                idf.add(Math.log((double) documents.size() / (double) documentValue));
            }
        }
        return idf;
    }

 /*   public double[] tfIdfForTerm(double[] tf, ArrayList<Double> idf) {
        double[] tf_idf = new double[tf.length];
        for (int i = 0; i < tf.length; i++) {
            if(tf[i] == 0 || idf.get(i) == 0 ) {
                tf_idf[i] = 0;
                continue;
            }
            tf_idf[i] = tf[i] * idf.get(i);
        }
        return tf_idf;
    }

*/

    public ArrayList<BigDecimal> tfIdfForTerm(double[] tf, ArrayList<Double> idf) {
        ArrayList<BigDecimal> tf_idf = new ArrayList<>();
        for (int i = 0; i < tf.length; i++) {
            if(tf[i] == 0 || idf.get(i) == 0 ) {
                tf_idf.add(new BigDecimal(0));
                continue;
            }
             tf_idf.add(new BigDecimal(tf[i]).multiply(new BigDecimal(idf.get(i))));
        }
        return tf_idf;
    }
}