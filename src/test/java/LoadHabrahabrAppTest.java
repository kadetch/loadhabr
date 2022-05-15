import org.junit.jupiter.api.Test;
import ru.kadetch.Grabber;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadHabrahabrAppTest {

    private final String validURL = "http://habr.com/ru";

    //    @Test
//    public void testGrabber(){
//        testURLValid();
//        testCreateBaseDir();
//    }
    @Test
    public void testURLValid() {

        String invalidURL = "ftp://jdjdj.ru";

        Grabber habrahabr = new Grabber(validURL);
        assertEquals(validURL, habrahabr.getLoadUrl());

        habrahabr = new Grabber(invalidURL);
        assertEquals(null, habrahabr.getLoadUrl());
    }

    @Test
    public void testCreateBaseDir() {

        String homeUserDir = System.getProperty("user.home");
        String pathDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String pathBaseDir = homeUserDir + File.separator + "habrhabr" + File.separator + pathDate;
        Path pathDir = Paths.get(pathBaseDir);


        Grabber habrahabr = new Grabber(validURL);
        assertEquals(pathBaseDir, habrahabr.getPathBaseDir());
        assertTrue(Files.exists(pathDir));

        if (Files.exists(pathDir)) {
            try {
                Files.delete(pathDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetPages() {
        Grabber habrahabr = new Grabber(validURL);
        habrahabr.start();
        assertEquals(50, habrahabr.getPages());
    }
}
