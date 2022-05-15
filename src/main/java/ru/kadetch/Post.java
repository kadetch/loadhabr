package ru.kadetch;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Post {
    private int id;
    private String url;
    private String title;
    private String titleHash;
    private String author;
    private String annotation;
    private String content;
    private LocalDateTime published;

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Post(int id, String title, String url) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.titleHash = generateTitleHash(title);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleHash() {
        return titleHash;
    }

    private String generateTitleHash(String title) {
        StringBuilder hashString = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(title.getBytes());
            byte[] hash = md5.digest(); //считаем хеш
            hashString.append( new String(hash, "UTF-8")); //конвертируем в строк
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.out.println("NOT MD5 ");
            Logger.getLogger(Grabber.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hashString.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && title.equals(post.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Post {" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
//                ", annotation='" + annotation + '\'' +
                ", date='" + published + '\'' +
                '}';
    }
}
