package com.example.coolrss.dbhelper.repository;

/*
 * Created by dutnguyen on 4/22/2020.
 */

import android.database.sqlite.SQLiteOpenHelper;

import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.model.RSSItem;

import java.util.ArrayList;
import java.util.List;

public class RSSItemRepository {
    private String RSS_ITEM_REPOSITORY_TAG = RSSItemRepository.class.getSimpleName();
    private static RSSItemRepository instance = null;
    private static SQLiteOpenHelper sqLiteOpenHelper = null;

    public static synchronized RSSItemRepository getInstance(AppDatabaseHelper databaseHelper) {
        if (instance == null) {
            instance = new RSSItemRepository(databaseHelper);
        }
        return instance;
    }

    private RSSItemRepository(AppDatabaseHelper databaseHelper) {
        sqLiteOpenHelper = databaseHelper;
    }

    public List<RSSItem> getListItems(String id) {
        // TODO: getListItems
        return new ArrayList<>();
    }

    public boolean addListItems(List<RSSItem> listRSSItems, String id) {
        // TODO: addListItems
        return true;
    }
}
