package com.example.coolrss.dbhelper;

/*
 * Created by dutnguyen on 4/22/2020.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "RSS.db";
    static final int DATABASE_VERSION = 1;

    private static AppDatabaseHelper instance = null;

    public static synchronized AppDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseHelper(context);
        }
        return instance;
    }

    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private String SQL_INT_STR = "INTEGER";
    private String SQL_TEXT_STR = "TEXT";
    private String SQL_DATE_TIME_STR = "INTEGER";
    private String SQL_REAL_STR = "REAL";
    private String SQL_CREATE_TABLE_STR = "CREATE TABLE IF NOT EXISTS";
    private String SQL_INTEGER_PRIMARY_KEY = "INTEGER PRIMARY KEY";
    private String SQL_TEXT_PRIMARY_KEY = "TEXT PRIMARY KEY";
    private String SQL_INTEGER_AUTO_INCREMENT = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private String SQL_DROP_TABLE_STR = "DROP TABLE IF EXISTS";
    private String SQL_ALTER_TABLE_STR = "ALTER TABLE";
    private String SQL_UPDATE_STR = "UPDATE ";
    private String SQL_FROM = "FROM";
    private String SQL_ADD = "ADD";

    public static class RSSFeedTable {
        public static final String TABLE_NAME = "RSSFeed";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LAST_BUILD_DATE = "last_build_date";
    }

    public static class RSSItemTable {
        public static final String TABLE_NAME = "RSSItem";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PUBLIC_DATE = "public_date";
        public static final String COLUMN_RSS_FEED_ID = "rss_feed_id";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createRSSFeedTable(db);
        createRSSItemTable(db);
    }

    private void createRSSFeedTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STR + " " + RSSFeedTable.TABLE_NAME + "(" +
                RSSFeedTable.COLUMN_ID + " " + SQL_INTEGER_PRIMARY_KEY + ", " +
                RSSFeedTable.COLUMN_TITLE + " " + SQL_TEXT_STR + ", " +
                RSSFeedTable.COLUMN_LINK + " " + SQL_TEXT_STR + ", " +
                RSSFeedTable.COLUMN_IMAGE + " " + SQL_TEXT_STR + ", " +
                RSSFeedTable.COLUMN_DESCRIPTION + " " + SQL_TEXT_STR + ", " +
                RSSFeedTable.COLUMN_LAST_BUILD_DATE + " " + SQL_TEXT_STR + ")");
    }

    private void createRSSItemTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STR + " " + RSSItemTable.TABLE_NAME + "(" +
                RSSItemTable.COLUMN_ID + " " + SQL_INTEGER_PRIMARY_KEY + ", " +
                RSSItemTable.COLUMN_TITLE + " " + SQL_TEXT_STR + ", " +
                RSSItemTable.COLUMN_LINK + " " + SQL_TEXT_STR + ", " +
                RSSItemTable.COLUMN_IMAGE + " " + SQL_TEXT_STR + ", " +
                RSSItemTable.COLUMN_DESCRIPTION + " " + SQL_TEXT_STR + ", " +
                RSSItemTable.COLUMN_PUBLIC_DATE + " " + SQL_TEXT_STR + ", " +
                RSSItemTable.COLUMN_RSS_FEED_ID + " " + SQL_TEXT_STR + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            cleanDB(db);
            // alter table

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void cleanDB(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_TABLE_STR + " " + RSSFeedTable.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_STR + " " + RSSItemTable.TABLE_NAME);
        onCreate(db);
    }
}
