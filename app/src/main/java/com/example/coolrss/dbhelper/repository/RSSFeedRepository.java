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
import com.example.coolrss.model.RSSFeed;
import com.example.coolrss.model.RSSItem;
import com.example.coolrss.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RSSFeedRepository {
    private String RSS_FEED_REPOSITORY_TAG = RSSFeedRepository.class.getSimpleName();
    private static RSSFeedRepository instance = null;
    private static SQLiteOpenHelper sqLiteOpenHelper = null;

    public static synchronized RSSFeedRepository getInstance(AppDatabaseHelper databaseHelper) {
        if (instance == null) {
            instance = new RSSFeedRepository(databaseHelper);
        }
        return instance;
    }

    private RSSFeedRepository(AppDatabaseHelper databaseHelper) {
        sqLiteOpenHelper = databaseHelper;
    }

    public void add(RSSFeed feed) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        boolean isSuccess = false;
        try {
            int id = isExistFeed(feed);
            if (id != -1) {
                update(feed, id);
                isSuccess = true;
            } else {
                ContentValues values = new ContentValues();
                values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_TITLE, feed.getTitle());
                values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_LINK, feed.getLink());
                values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_IMAGE, feed.getImage());
                values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_DESCRIPTION, feed.getDescription());

                // convert date from Object format to SQL format
                String dateStrOfFeed = feed.getLastBuildDateStr();
                Date dateOfFeed = StringUtils.getDateFromString(dateStrOfFeed);
                String dateSQLStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
                values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_LAST_BUILD_DATE, dateSQLStrOfFeed);
                long retId = db.insert(AppDatabaseHelper.RSSFeedTable.TABLE_NAME, null, values);
                if (retId != -1) {
                    // TODO: add list
                    RSSItemRepository repository = RSSItemRepository.getInstance((AppDatabaseHelper) sqLiteOpenHelper);
                    isSuccess = repository.addListItems(feed.getListRSSItems(), Long.toString(retId));
                }
            }
            if (isSuccess) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
    }

    public void add(Iterable<RSSFeed> listFeeds) {
        for (RSSFeed feed : listFeeds) {
            add(feed);
        }
    }

    // Check if there is any RSS feed with a link and created before input RSS feed
    public int isExistFeed(RSSFeed feed) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + AppDatabaseHelper.RSSFeedTable.TABLE_NAME +
                    " WHERE " + AppDatabaseHelper.RSSFeedTable.COLUMN_LINK + " = ?" +
                    " AND " + AppDatabaseHelper.RSSFeedTable.COLUMN_LAST_BUILD_DATE + " <= ?";

            // convert date from Object format to SQL format
            String dateStrOfFeed = feed.getLastBuildDateStr();
            Date dateOfFeed = StringUtils.getDateFromString(dateStrOfFeed);
            String dateSQLStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
            Cursor cursor = db.rawQuery(query, new String[]{feed.getLink(), dateSQLStrOfFeed});
            if (cursor.moveToFirst()) {
                String idValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_ID));
                cursor.close();
                return Integer.parseInt(idValue);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(RSS_FEED_REPOSITORY_TAG, "Exception " + e.getMessage(), e);
        }
        return -1;
    }

    public void update(RSSFeed feed, int rowId) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_TITLE, feed.getTitle());
            values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_IMAGE, feed.getImage());
            values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_DESCRIPTION, feed.getDescription());

            // convert date from Object format to SQL format
            String dateStrOfFeed = feed.getLastBuildDateStr();
            Date dateOfFeed = StringUtils.getDateFromString(dateStrOfFeed);
            String dateSQLStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
            values.put(AppDatabaseHelper.RSSFeedTable.COLUMN_LAST_BUILD_DATE, dateSQLStrOfFeed);
            db.update(AppDatabaseHelper.RSSFeedTable.TABLE_NAME, values, AppDatabaseHelper.RSSFeedTable.COLUMN_ID + " = ?", new String[]{Integer.toString(rowId)});
            // TODO: add list
            RSSItemRepository repository = RSSItemRepository.getInstance((AppDatabaseHelper) sqLiteOpenHelper);
            boolean isSuccess = repository.addListItems(feed.getListRSSItems(), Integer.toString(rowId));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public boolean isEmpty() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + AppDatabaseHelper.RSSFeedTable.TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(RSS_FEED_REPOSITORY_TAG, "Exception " + e.getMessage(), e);
        }
        return true;
    }

    public RSSFeed getFeed(String link) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + AppDatabaseHelper.RSSFeedTable.TABLE_NAME +
                    " WHERE " + AppDatabaseHelper.RSSFeedTable.COLUMN_LINK + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{link});
            if (cursor.moveToFirst()) {
                return getFeedFromCursor(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(RSS_FEED_REPOSITORY_TAG, "Exception " + e.getMessage(), e);
        }
        return null;
    }

    private RSSFeed getFeedFromCursor(Cursor cursor) {
        String idValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_ID));
        String titleValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_TITLE));
        String linkValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_LINK));
        String imageValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_IMAGE));
        String descriptionValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_DESCRIPTION));
        String dateValue = cursor.getString(cursor.getColumnIndex(AppDatabaseHelper.RSSFeedTable.COLUMN_LAST_BUILD_DATE));
        RSSItemRepository repository = RSSItemRepository.getInstance((AppDatabaseHelper) sqLiteOpenHelper);
        List<RSSItem> listItems = new ArrayList<>(repository.getListItems(idValue));
        RSSFeed feed = new RSSFeed();
        feed.setTitle(titleValue);
        feed.setLink(linkValue);
        feed.setDescription(descriptionValue);
        feed.setImage(imageValue);

        // convert date from SQL format to Object format
        Date dateOfFeed = StringUtils.getDateFromSQLString(dateValue);
        String dateStrOfFeed = StringUtils.getSQLStringFromDate(dateOfFeed);
        feed.setLastBuildDate(dateStrOfFeed);
        feed.setListRSSItems(listItems);
        return feed;
    }

    public List<RSSFeed> getAll() {
        List<RSSFeed> listFeeds = new ArrayList<RSSFeed>();
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        String query = "SELECT * FROM " + AppDatabaseHelper.RSSFeedTable.TABLE_NAME +
                " ORDER BY " + AppDatabaseHelper.RSSFeedTable.COLUMN_LAST_BUILD_DATE + " DESC";
        // TODO: sort by clicked date time
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return listFeeds;
        }
        if (cursor.moveToFirst()) {
            do {
                RSSFeed feed = null;
                feed = getFeedFromCursor(cursor);
                if (feed != null)
                    listFeeds.add(feed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listFeeds;
    }
}
