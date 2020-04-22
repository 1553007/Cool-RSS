package com.example.coolrss.dbhelper.repository;

/*
 * Created by dutnguyen on 4/22/2020.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.coolrss.dbhelper.AppDatabaseHelper;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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

    public List<RSSItem> getListItems(String feedId) {
        // TODO: getListItems
        List<RSSItem> listItems = new ArrayList<>();
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        String query = "SELECT * FROM " + AppDatabaseHelper.RSSItemTable.TABLE_NAME +
                " WHERE " + AppDatabaseHelper.RSSItemTable.COLUMN_RSS_FEED_ID + " = ?" +
                " ORDER BY " + AppDatabaseHelper.RSSItemTable.COLUMN_PUBLIC_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{feedId});
        if (cursor.getCount() <= 0) {
            cursor.close();
            return listItems;
        }
        if (cursor.moveToFirst()) {
            do {
                RSSItem item = null;
                item = getItemFromCursor(cursor);
                if (item != null)
                    listItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listItems;
    }

    // add list of RSS Items into db
    public void add(List<RSSItem> listRSSItems, String feedId) {
        for (RSSItem item : listRSSItems) {
            add(item, feedId);
        }
    }

    // add a RSS Item into db
    private void add(RSSItem item, String feedId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        boolean isSuccess = false;
        try {
            int id = isExistFeed(item);
            if (id != -1) {
                update(item, Integer.toString(id), feedId);
                isSuccess = true;
            } else {
                ContentValues values = new ContentValues();
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_TITLE, item.getTitle());
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_LINK, item.getLink());
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_IMAGE, item.getImage());
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_DESCRIPTION, item.getDescription());

                // convert date from Object format to SQL format
                String dateStrOfFeed = item.getPubDateStr();
                Date dateOfFeed = StringUtils.getDateFromString(dateStrOfFeed);
                String dateSQLStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_PUBLIC_DATE, dateSQLStrOfFeed);
                values.put(AppDatabaseHelper.RSSItemTable.COLUMN_RSS_FEED_ID, feedId);
                long retId = db.insert(AppDatabaseHelper.RSSItemTable.TABLE_NAME, null, values);
                if (retId != -1) {
                    // TODO: add list
                    isSuccess = true;
                }
            }
            if (isSuccess) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
    }

    // update a RSS Item of a RSS-Feed-id with a specific id
    private void update(RSSItem item, String itemId, String feedId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_TITLE, item.getTitle());
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_LINK, item.getLink());
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_IMAGE, item.getImage());
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_DESCRIPTION, item.getDescription());

            // convert date from Object format to SQL format
            String dateStrOfFeed = item.getPubDateStr();
            Date dateOfFeed = StringUtils.getDateFromString(dateStrOfFeed);
            String dateSQLStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_PUBLIC_DATE, dateSQLStrOfFeed);
            values.put(AppDatabaseHelper.RSSItemTable.COLUMN_RSS_FEED_ID, feedId);

            db.update(AppDatabaseHelper.RSSItemTable.TABLE_NAME, values, AppDatabaseHelper.RSSItemTable.COLUMN_ID + " = ?", new String[]{itemId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Check if there is any RSS item with a specific link
    public int isExistFeed(RSSItem item) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + AppDatabaseHelper.RSSItemTable.TABLE_NAME +
                    " WHERE " + AppDatabaseHelper.RSSItemTable.COLUMN_LINK + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{item.getLink()});
            if (cursor.moveToFirst()) {
                String idValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_ID));
                cursor.close();
                return Integer.parseInt(idValue);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(RSS_ITEM_REPOSITORY_TAG, "Exception " + e.getMessage(), e);
        }
        return -1;
    }

    private RSSItem getItemFromCursor(Cursor cursor) {
        String idValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_ID));
        String titleValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_TITLE));
        String linkValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_LINK));
        String imageValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_IMAGE));
        String descriptionValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_DESCRIPTION));
        String dateValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSItemTable.COLUMN_PUBLIC_DATE));

        // convert date from SQL format to Object format
        Date dateOfItem = StringUtils.getDateFromSQLString(dateValue);
        String dateStrOfItem = StringUtils.getStringFromDate(dateOfItem);

        RSSItem item = new RSSItem(titleValue, descriptionValue, linkValue, dateStrOfItem, imageValue);
        return item;
    }
}
