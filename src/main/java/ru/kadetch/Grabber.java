package ru.kadetch;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Grabber {

    private int pages = 1;
    private String loadUrl;
    private final String pathBaseDir;
    private final Map<Integer, Post> posts = new HashMap<Integer, Post>();

//    private GrabberPost grabberPost;

//    private SavePost savePost;

//    private OutputStream hashFile;

//    static Set<String> hashes = new HashSet<>(); //список хешей прочтёных цитат

//    private MessageDigest md5;

    public Grabber(String loadUrl) {
        if (isUrlValid(loadUrl)) {
            this.loadUrl = loadUrl;
        }

        pathBaseDir = createBaseDir();


//
//        grabberPost = new GrabberPost(baseDir);
//        savePost = new SavePost((baseDir));
//
//        readHash();
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

    private boolean isUrlValid(String urlText) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        return urlValidator.isValid(urlText);
    }

    public int getPages() {
        return pages;
    }

    public Document loadIndexPage() {
        Document doc;
        try {
            doc = Jsoup.connect(loadUrl).get();
            Element navigation = doc.getElementsByClass("tm-pagination__page").last();
            pages = Integer.parseInt(navigation.text());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Всего страниц: " + pages);
        System.out.println();
        return doc;
    }

    private void loadPostsFromPage(Document page) throws IOException {

        int currentPage = 0;
        while (pages > currentPage) {

            loadPostList(page);

            page = getNextPage(page);

            if (null != page)
                currentPage++;
        }
    }

    private void loadPostList(Document page) {
        AtomicReference<Integer> id = new AtomicReference<>(0);
        StringBuilder annotation = new StringBuilder();

        Elements postList = page.getElementsByTag("article");
        postList.stream()
                .map(article -> getElement(id, annotation, article))
                .filter(Objects::nonNull)
                .map(shortPost -> createPost(id, annotation, shortPost))
                .peek(post -> posts.put(post.getId(), post))
                .forEach(System.out::println);
    }

    private Document getNextPage(Document page) throws IOException {
        StringBuilder urlNextPage = new StringBuilder();
        urlNextPage.append(loadUrl, 0, loadUrl.lastIndexOf("/"));
        urlNextPage.append(page.getElementById("pagination-next-page").attr("href"));
        page = Jsoup.connect(urlNextPage.toString()).get();
        System.out.println("PAGE: " + urlNextPage);
        return page;
    }

    private Element getElement(AtomicReference<Integer> id, StringBuilder annotation, Element article) {
        id.set(Integer.valueOf(article.attr("id")));
        try {
            annotation.append(article.getElementsByClass("article-formatted-body").first().html());
        }catch (NullPointerException ignored){

        }
        return article.getElementsByClass("tm-article-snippet__title").first();
    }

    private Post createPost(AtomicReference<Integer> id, StringBuilder annotation, Element shortPost) {
        String hostUrl = loadUrl.substring(0, loadUrl.lastIndexOf("/"));
        String title = shortPost.getElementsByTag("span").first().text();
        String url = hostUrl + shortPost.getElementsByTag("a").first().attr("href");

        Post post = new Post(id.get(), title, url);
        post.setAnnotation(annotation.toString());

        annotation.delete(0, annotation.length());
        return post;
    }

    public void start() {
        if (null != loadUrl) {
            Document doc = loadIndexPage();
            try {
                loadPostsFromPage(doc);
                PostLoader loader = new PostLoader(pathBaseDir);
                posts.entrySet().stream().forEach(p -> loader.start(p.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLoadUrl() {
        if (loadUrl != null)
            return loadUrl;
        else return null;
    }

    public String getPathBaseDir() {
        return pathBaseDir;
    }
}
