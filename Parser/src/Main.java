import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.Map.Entry;

public class Main {

    private static String directoryDocumentsName = "documents";
    private static String directoryProcessedDocumentsName = "processedDocuments";

    private static String readLineByLineJava8(String filePath)  {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
         //   e.printStackTrace();
        }
        return contentBuilder.toString();
    }


    public static void main(String[] args) throws TikaException, IOException, SAXException {

//        init();
//        downloadDocuments();
//        processFiles();
        String[] keywords = readLineByLineJava8("keywords.txt").split("\\s");

        System.out.println("Enter your query: ");
        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();
        System.out.println("Your query is: " + query);

        List<List<String>> documents = new ArrayList<>();

        int docNum = 1000;
        ArrayList<Document> documents1 = new ArrayList<>();

        for (int i = 1; i <= docNum; i++) {
            String document = "processedDocuments/doc" + i + ".txt";
            String content = readLineByLineJava8(document);
            documents.add(tokenization(content));
            documents1.add(new Document(i));
        }
        List<String> queryTokenized = Arrays.asList(query.split("\\s"));
        TF_IDF_Ranking tfIdf = new TF_IDF_Ranking();

        ArrayList<Double> idf = tfIdf.idf(documents, Arrays.asList(keywords));
        for(Document document : documents1) {
            double[] tf = tfIdf.tf(documents.get(document.getNumber()-1), Arrays.asList(keywords));

            ArrayList<BigDecimal> tfIdfVal = tfIdf.tfIdfForTerm(tf, idf);
            document.setTf_idf(tfIdfVal);
        }

        double[] tf_query = tfIdf.tf(queryTokenized, Arrays.asList(keywords));
        ArrayList<BigDecimal> tfidf_query = tfIdf.tfIdfForTerm(tf_query,idf);

        List<Score> scores = new ArrayList<>();




//        Map rank = new HashMap<Integer, Double>();
        for(Document document : documents1){
            scores.add(
                    new Score (document.getNumber(),cosineSimilarity(document.getTf_idf(),tfidf_query) ) );
        }


        Collections.sort(scores);
        //  Collections.reverse(scores);


        String[] urls = readLineByLineJava8("pages.txt").split("\n");
        List<Score> scores_norm = new ArrayList<>();

            int it = 0;
        for(Score score : scores){
            if(score.getSimilarity()==0) break;
            System.out.println(score.getNumber());
            System.out.println(score.getSimilarity());
            System.out.println(urls[score.getNumber()-1]);
            it++;
            if(it>10) break;
        }
        //       Map rank_sorted = new HashMap<Integer, Double>();

        //       entriesSortedByValues(rank);



        //   List keys = new ArrayList(rank.values());

        //    Collections.sort(keys);
        //    Collections.reverse(keys);


    }

    static <K, V extends Comparable<? super V>> List<Entry<String, Integer>> entriesSortedByValues(Map<String, Integer> map) {

        List<Entry<String, Integer>> sortedEntries = new ArrayList<Entry<String, Integer>>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                return e2.getKey().compareTo(e1.getKey());
            }
        });

        return sortedEntries;
    }



  /*  public static double cosineSimilarity(ArrayList<BigDecimal> vectorA, ArrayList<BigDecimal> vectorB) {
        double dotProduct =0;
        BigDecimal normA = new BigDecimal(0.0);
        BigDecimal normB = new BigDecimal(0.0);
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct+=vectorA.get(i).multiply(vectorB.get(i)).doubleValue();
            normA.add(vectorA.get(i).pow( 2));
            normB.add(vectorB.get(i).pow( 2));
        }
        if(Math.sqrt(normA.doubleValue()) * Math.sqrt(normB.doubleValue()) == 0)
            return 0;
        else
            return dotProduct / (Math.sqrt(normA.doubleValue()) * Math.sqrt(normB.doubleValue()));
    }
*/


  public static double cosineSimilarity(ArrayList<BigDecimal> vectorA, ArrayList<BigDecimal> vectorB){
      double similarity = 0;
      double documentsValue = 0;
      double queryValue = 0;
      for (int i = 0; i < vectorB.size(); i++) {
          similarity += (vectorB.get(i).multiply(vectorA.get(i))).doubleValue();
          documentsValue += Math.pow(vectorA.get(i).doubleValue(), 2);
          queryValue += Math.pow(vectorB.get(i).doubleValue(), 2);
      }
      similarity /= (Math.sqrt(documentsValue) * Math.sqrt(queryValue));
      return similarity;
  }
    private static List<String> tokenization(String text) throws IOException {
        File file = new File("model/en-token.bin");
        TokenizerModel tokenizerModel = new TokenizerModel(file);
        TokenizerME tokenizerME = new TokenizerME(tokenizerModel);

        return Arrays.asList(tokenizerME.tokenize(text));
    }

    private static void init() {
        File documentsDirectory = new File(directoryDocumentsName);
        File documentsProcessedDirectory = new File(directoryProcessedDocumentsName);
        if (!documentsDirectory.exists()) {
            try {
                documentsDirectory.mkdir();
            } catch (SecurityException se) {
                se.getStackTrace();
            }
        }
        if (!documentsProcessedDirectory.exists()) {
            try {
                documentsProcessedDirectory.mkdir();
            } catch (SecurityException se) {
                se.getStackTrace();
            }
        }
    }

    private static void downloadDocuments() throws FileNotFoundException {
        Downloader downloader = new Downloader();
        Scanner scanner = null;
        scanner = new Scanner(new File("pages.txt"));


        int docValue = 0;
        while (scanner.hasNextLine()) {
            docValue++;
            File docFile = new File(directoryDocumentsName + "/doc" + docValue + ".html");
            if (!docFile.exists()) {
                String url = scanner.nextLine();
                downloader.save(url, directoryDocumentsName, "doc" + docValue);
            } else {
                System.out.println("Istnieje doc" + docValue + ".html");
            }
        }
    }

    private static void processFiles() throws TikaException, IOException, SAXException {
        Processor processor = new Processor();

        File directory = new File(directoryDocumentsName);
        File[] files = directory.listFiles();
        String keywords = "";
        if (files != null) {
            for (File file : files) {
                keywords += " " + processor.processHtml(file.getName(), directoryDocumentsName, directoryProcessedDocumentsName);
            }
        }
        String[] filteredKeywords = keywords.split(" ");

        HashSet<String> dictionary = new HashSet<String>();
        for (String keyword : filteredKeywords) {
            if (!"\n\t!@#$%^&*()_+-=[]{}|;',./<>?:".contains(keyword)) {
                dictionary.add(keyword.toLowerCase());
            }
        }
        saveKeywords(dictionary);
    }

    private static void saveKeywords(HashSet<String> keywords) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter("keywords.txt");
        for (String keyword : keywords) {
            printWriter.write(keyword + "\n");
        }
        printWriter.close();
    }
}