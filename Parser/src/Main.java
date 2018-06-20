import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    private static String directoryDocumentsName = "documents";
    private static String directoryProcessedDocumentsName = "processedDocuments";

    public static void main(String[] args) throws TikaException, IOException, SAXException {
        init();
        downloadDocuments();
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
            String url = scanner.nextLine();
            if (!docFile.exists()) {
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
