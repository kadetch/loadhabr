package ru.kadetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PostLoader {

    private static final String IMAGES_DIR = "post_image/";

    private static final int IMAGE_FILE_NAME_LENGHT = 32;

    private final String BASE_DIR;

    private Document doc;

    private Post post;
    private List<String> pathOfImages;

    public PostLoader() {
        BASE_DIR = "";
    }

    public PostLoader(String dir) {
        BASE_DIR = dir + "/";
    }

    public void start(Post inPost) {
        this.post = inPost;

        // Загрузка страницы
        try {
            doc = Jsoup.connect(post.getUrl()).get();

            post.setAuthor(getPostAuthor());
            post.setPublished(getPostPublished());
            post.setContent(getPostContent());

//            Elements itemPosts = doc.getElementsByClass("shortcuts_item");
//
//            if (itemPosts != null) {
//
//                post.setId(Integer.parseInt(itemPosts.attr("id").replaceAll("post_", "")));
//
//                for (Element postElement : itemPosts) {
//                    if (postElement != null) {
//                        // Дата публикации
//                        String published = postElement.getElementsByClass("published").first().text();
//                        //System.out.println(published);
//
//                        // Теги статьи
////                    String hubs = postElement.getElementsByClass("hubs").first().html();
////                    System.out.println(hubs);
//                        for (Element hub : postElement.getElementsByClass("hub")) {
//                            //System.out.println(hub.text());
////                            post.setHub(hub.text());
//                        }
//                        //System.out.println();
//

//                    }
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> searchImages(Element content) {
        List<String> images = content.getElementsByTag("img")
                .stream()
                .map(image -> image.attr("src",
                        downloadImage(image.attr("src"))
                        )
                )
                .map(image -> image.attr("src"))
                .collect(Collectors.toList());
        return images;
    }

    private String getPostContent(Document doc) {
        Element content = doc.getElementById("post-content-body");
        pathOfImages = searchImages(content);
//        post.setContent(content.html());
        return content.html();
    }

    private String getPostContent() {
        return getPostContent(doc);
    }

    private String getPostAuthor(Document doc) {
        return doc.getElementsByClass("tm-user-info").first().text();
    }

    private String getPostAuthor() {
        return getPostAuthor(doc);
    }

    private LocalDateTime getPostPublished(Document doc) {
        return LocalDateTime.parse(doc
                        .getElementsByClass("tm-article-snippet__datetime-published")
                        .first().getElementsByTag("time")
                        .first().attr("datetime"),
                DateTimeFormatter.ISO_DATE_TIME);
    }

    private LocalDateTime getPostPublished() {
        return getPostPublished(doc);
    }

    private String getPathForImage(String srcImage) {
        StringBuilder returnUrl = new StringBuilder();
        String imageFileName = srcImage.substring(srcImage.lastIndexOf("/") + 1);
        returnUrl.append(mkDirImage(imageFileName)).append(imageFileName);
        return returnUrl.toString();
    }

    private String getImageFormat(String imageFileName) {
        return imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
    }

    private String downloadImage(String urlString) {
        String imageFileName = getPathForImage(urlString);
        try {
            URL url = new URL(urlString);
            BufferedImage inImage = ImageIO.read(url);
            ImageIO.write(inImage, getImageFormat(imageFileName), new File(BASE_DIR + imageFileName));
        } catch (MalformedURLException e) {
            Logger.getLogger(PostLoader.class.getName()).log(Level.SEVERE, null, e);
//        } catch (IllegalArgumentException e) {
//            returnUrl = "";
//            System.out.println("ERROR: File is not image. Error message: " + e.getMessage());
        } catch (IOException e) {
//            returnUrl = "";
            System.out.println("ERROR: " + e.getMessage());
        }
        return imageFileName;
    }

    private String mkDirImage(String imageFileName) {
        String returnUrl;
        int sizeImageFileName = imageFileName.lastIndexOf('.');
        if (IMAGE_FILE_NAME_LENGHT == sizeImageFileName) {
            returnUrl = IMAGES_DIR + imageFileName.substring(0, 3) + "/" + imageFileName.substring(3, 6) + "/" + imageFileName.substring(6, 9) + "/";
        } else {
            returnUrl = IMAGES_DIR + "other/";
        }
        new File(BASE_DIR + returnUrl).mkdirs();
        return returnUrl;
    }

    public List<String> getPathOfImages() {
        return pathOfImages;
    }
}

