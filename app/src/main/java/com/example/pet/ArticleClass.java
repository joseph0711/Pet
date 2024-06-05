package com.example.pet;

import java.io.Serializable;

public class ArticleClass implements Serializable {
    private String title;
    private String author;
    private String date;
    private String excerpt;
    private String markdownContent;

    public ArticleClass(String title, String author, String date, String excerpt, String markdownContent) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.excerpt = excerpt;
        this.markdownContent = markdownContent;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getExcerpt() {
        return excerpt;
    }
    public String getMarkdownContent() {
        return markdownContent;
    }
}
