import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.kadetch.Post;
import ru.kadetch.PostLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


public class testPostLoader {

    private Post post = new Post(661465,
            "Установка Sonatype Nexus с SSL",
            "http://habr.com/ru/company/first/blog/661465/");
    private PostLoader postLoader = new PostLoader(createBaseDir());



    private testPostLoader(){
            postLoader.start(post);
    }

    @Test
    public void testCorrectLoadPostFromPage() {

        assertEquals("1shaman", post.getAuthor());  //Author
        assertEquals("Установка Sonatype Nexus с SSL", post.getTitle());//Title
        assertEquals("http://habr.com/ru/company/first/blog/661465/", post.getUrl());   //Url
        assertEquals(LocalDateTime.parse("2022-04-21T08:01:33"), post.getPublished());  //Date of publication
        assertNotNull(post.getContent().length());
//        System.out.println(post.getContent());

    }

//    @Test
    public void testUncorrectLoadPostFromPage() {

        assertNotEquals("1s2222", post.getAuthor());  //Author
        assertNotEquals("Установка Nexus с SSL", post.getTitle());//Title
        assertNotEquals("http://habr.com/ru/company/first/blog/661435/", post.getUrl());   //Url
        assertNotEquals(LocalDateTime.parse("2022-04-23T08:01:33"), post.getPublished());  //Date of publication
    }

    private String createBaseDir() {
        final String pathBaseDir;
        String homeUserDir = System.getProperty("user.home");
        String pathDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        pathBaseDir = homeUserDir + File.separator + "habrhabr" + File.separator + pathDate;
        File dir = new File(pathBaseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return pathBaseDir;
    }
}
