package com.example.coolrss.model;

/*
 * Created by dutnguyen on 4/17/2020.
 */

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class RSSItem implements Comparable<RSSItem> {

    @Element(name = "title")
    private String title;

    @Element(name = "description", data = true)
    private String description;

    @Element(name = "pubDate")
    private String pubDate;

    @Element(name = "link")
    private String link;

    private String image;

    public RSSItem() {
    }

    public RSSItem(String title, String description, String link, String pubDate, String image) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.image = image;
        this.pubDate = pubDate;
    }

//    public RSSItem(String title, String link, String description, String image, String pubDate) {
//        this.title = title;
//        this.link = link;
//        this.description = description;
//        this.image = image;
//        this.pubDate = pubDate;
//    }

    @Override
    public int compareTo(RSSItem o) {
        return title.compareTo(o.title);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDateStr() {
        return pubDate;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }
}
