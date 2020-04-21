package com.example.coolrss.model;

/*
 * Created by dutnguyen on 4/18/2020.
 */

import com.example.coolrss.utils.StringUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "rss", strict = false)
public class RSSFeed {

    @Path("channel")
    @Element(name = "title")
    private String title;

    @Path("channel")
    @Element(name = "image")
    private String image;

    @Path("channel")
    @Element(name = "link")
    private String link;

    @Path("channel")
    @Element(name = "description")
    private String description;

    @Path("channel")
    @ElementList(name = "item", inline = true)
    private List<RSSItem> listRSSItems;

    public RSSFeed() {

    }

    public RSSFeed(String title, List<RSSItem> listRSSItems) {
        this.title = title;
        this.listRSSItems = listRSSItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RSSItem> getListRSSItems() {
        return listRSSItems;
    }

    public void setListRSSItems(List<RSSItem> listRSSItems) {
        this.listRSSItems = listRSSItems;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        String startStr = "http://";
        if (link.contains("https://")) {
            startStr = "https://";
        }
        this.link = link.replace(startStr, "");
        this.image = StringUtils.getLogoInWebsite(link);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
