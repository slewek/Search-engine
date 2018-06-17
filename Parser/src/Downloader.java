import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Downloader {
    public void save(String url, String directory, String name) {
        URL website = null;
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            website = new URL(url);
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(directory + "/" + name + ".html");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
