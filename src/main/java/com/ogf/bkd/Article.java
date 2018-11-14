package com.ogf.bkd;

import java.util.ArrayList;

public class Article {
    private String title;
    private String publicationName;
    private String publicationDate;
    private String url;
    private String abstractArticle;
    private ArrayList<String> creators;

    public Article(String title, String publicationName, String publicationDate, String url, String abstractArticle, ArrayList<String> creator) {
        this.title = title;
        this.publicationName = publicationName;
        this.publicationDate = publicationDate;
        this.url = url;
        this.abstractArticle = abstractArticle;
        this.creators = creator;
    }

    @Override
    public String toString() {
        StringBuilder article = new StringBuilder("*"+title+"*\n_");
        for (String creator: creators)
            article.append(creator+" ");
        article.append("_\n"+"ABSTRACT"+"\n"+abstractArticle+"\n_");
        article.append(publicationDate+"\n"+publicationName+"_\n");
        //article.append("article "+url);
        return article.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAbstractArticle() {
        return abstractArticle;
    }

    public void setAbstractArticle(String abstractArticle) {
        this.abstractArticle = abstractArticle;
    }

    public ArrayList<String> getCreators() {
        return creators;
    }

    public void setCreators(ArrayList creators) {
        this.creators = creators;
    }
}
