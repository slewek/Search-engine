import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static String directoryDocumentsName = "documents";
    private static String directoryProcessedDocumentsName = "processedDocuments";

    public static void main(String[] args) throws TikaException, IOException, SAXException {
        init();
        //downloadDocuments();
        processFiles();
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
        scanner = new Scanner(new File("../pages.txt"));


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
        if (files != null) {
            for (File file : files) {
                processor.processHtml(file.getName(), directoryDocumentsName, directoryProcessedDocumentsName);
            }
        }
    }
}
