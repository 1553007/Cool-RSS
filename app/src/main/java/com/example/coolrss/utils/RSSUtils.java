package com.example.coolrss.utils;

/*
 * Created by dutnguyen on 4/21/2020.
 */

import android.util.Xml;

import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.model.RSSItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RSSUtils {
    // Parse RSS Feed from a URL
    public static RSSFeed parseRSSFeedFromURL(String inputURL) throws XmlPullParserException,
            IOException {
        RSSFeed rssFeed = new RSSFeed();
        String title = "";
        String link = "";
        String description = "";
        String image = "";
        String pubDate = "";
        String currentTag = "";
        List<RSSItem> itemList = new ArrayList<>();

        URL url = new URL(inputURL);
        InputStream inputStream = url.openConnection().getInputStream();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (name != null) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equalsIgnoreCase("item") || name.equalsIgnoreCase("image")) {
                                currentTag = name;
                            }
                            if (!currentTag.equalsIgnoreCase("image") && parser.next() == XmlPullParser.TEXT) {
                                String result = parser.getText();
                                if (name.equalsIgnoreCase("title")) {
                                    title = result;
                                } else if (name.equalsIgnoreCase("description")) {
                                    image = StringUtils.getImageUrlInString(result);
                                    if (result.contains("</br>")) {
                                        description = result.substring(result.indexOf("</br>") + 5);
                                    } else {
                                        description = result;
                                    }
                                } else if (name.equalsIgnoreCase("link")) {
                                    link = result;
                                } else if (name.equalsIgnoreCase("pubDate")) {
                                    pubDate = result;
                                }
                                if (!title.isEmpty() && !description.isEmpty() && !link.isEmpty()) {
                                    if (currentTag.equalsIgnoreCase("item")) {
                                        itemList.add(new RSSItem(title, description, link, pubDate, image));
                                    } else {
                                        rssFeed.setTitle(title);
                                        rssFeed.setDescription(description);
                                        rssFeed.setLink(link);
                                        rssFeed.setLastBuildDate(StringUtils.getStringFromDate(Calendar.getInstance().getTime()));
                                    }
                                    title = "";
                                    link = "";
                                    description = "";
                                    pubDate = "";
                                    image = "";
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equalsIgnoreCase("item") || name.equalsIgnoreCase("image")) {
                                currentTag = "";
                            }
                            break;
                    }
                }
                eventType = parser.next();
            }
            rssFeed.setListRSSItems(itemList);
            return rssFeed;
        } finally {
            inputStream.close();
        }
    }
}
