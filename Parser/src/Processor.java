import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.text.Normalizer;
import java.util.regex.Pattern;

import java.io.*;

public class Processor {

    public String processHtml(String document, String inputDirectory, String outputDirectory) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(inputDirectory + "/" + document));
        ParseContext pcontext = new ParseContext();

        HtmlParser htmlparser = new HtmlParser();
        htmlparser.parse(inputstream, handler, metadata, pcontext);
        String keywords = metadata.get("keywords");

        String normalizedKeywords = "";
        try {
            String nfdNormalizedString = Normalizer.normalize(keywords, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            normalizedKeywords = pattern.matcher(nfdNormalizedString).replaceAll("");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        saveResult(document, outputDirectory, handler.toString(), normalizedKeywords);

        return normalizedKeywords;
    }

    private void saveResult(String fileName, String directory, String contents, String keywords) {

        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try {
            PrintWriter printWriter = new PrintWriter(directory + "/" + outName);
          //  printWriter.write(contents.trim().replaceAll("\\s{2,}", " ").replaceAll("\n", " "));
            printWriter.write("\n" + keywords.toLowerCase());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
