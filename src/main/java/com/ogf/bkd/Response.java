package com.ogf.bkd;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;

public class Response {
    private String keyWord;
    private boolean openaccess;
    private String country;
    private int numberArticle;

    private ArrayList<Article> records = new ArrayList<Article>();

    public Response(String query) {
        this.keyWord = query;
        this.openaccess =false;
        this.country = "";
        this.numberArticle = 10;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public boolean getOpenaccess() {
        return openaccess;
    }

    public void setOpenaccess(boolean openaccess) {
        this.openaccess = openaccess;
    }

    public String getCountry() {
        if (country.equals("")){
            return "all";
        }else
            return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getNumberArticle() {
        return numberArticle;
    }

    public void setNumberArticle(int numberArticle) {
        if (numberArticle <= 0) {
           throw new IllegalArgumentException("The number of articles can not be less than 1");
        } else if (numberArticle > 100) {
            throw new IllegalArgumentException("The number of articles can not be more than 100");
        }else
            this.numberArticle = numberArticle;
    }

    public void runQuery(){
        String countryQuery;
        if(!country.equals("") & country!=null){
            countryQuery= "country:\""+this.country+"\"";
        }else
            countryQuery="";
        //String openaccess= "openaccess:" + this.openaccess;
        String url=String.format("http://api.springer.com/meta/v1/json?q=keyword:\"%s\"openaccess:%s%s&p=%d&api_key=", keyWord, openaccess+"",countryQuery, numberArticle);
        url+="4de43c6d5b7579fdc21f83f37a9901bb";
        try( Reader reader = new InputStreamReader(new URL(url).openStream());) {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject)parser.parse(reader);
            initializeRecords((ArrayList<JSONObject>) object.get("records"));
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    private void initializeRecords(ArrayList<JSONObject> jsonObjects) {
        for (JSONObject object:jsonObjects){
            String title=(String)object.get("title");
            String publicationName = (String)object.get("publicationName");
            String publicationDate= (String)object.get("publicationDate");
            String url = (String)((ArrayList<JSONObject>)object.get("url")).get(1).get("value");
            String abstractArticle = (String)object.get("abstract");
            ArrayList<String> creators = new ArrayList<>();
            for (JSONObject creator : (ArrayList<JSONObject>)object.get("creators")){
                creators.add((String) creator.get("creator"));
            }
            this.records.add(new Article(title,publicationName,publicationDate,url,abstractArticle,creators));
        }
    }

    public ArrayList<Article> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Article> records) {
        this.records = records;
    }
}