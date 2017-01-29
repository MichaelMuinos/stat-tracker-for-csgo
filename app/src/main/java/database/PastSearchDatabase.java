package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.models.PastSearch;

public class PastSearchDatabase extends SQLiteOpenHelper {

    private static PastSearchDatabase instance;

    private static final String DATABASE_NAME = "past_search_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PAST_SEARCHES = "table_past_searches";

    private static final String PAST_SEARCH_GAMER_TAG = "past_search_gamer_tag";
    private static final String PAST_SEARCH_PROFILE_URL = "past_search_profile_url";
    private static final String PAST_SEARCH_STEAM_ID = "past_search_steam_id";
    private static final String PAST_SEARCH_LAST_LOG_ON = "past_search_last_log_on";

    // constructor protected so that there is no direct instantiation
    protected PastSearchDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized PastSearchDatabase getInstance(Context context) {
        if(instance == null)
            instance = new PastSearchDatabase(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_PAST_SEARCHES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PAST_SEARCHES +
                "(" +
                PAST_SEARCH_STEAM_ID + " TEXT PRIMARY KEY," +
                PAST_SEARCH_PROFILE_URL + " TEXT," +
                PAST_SEARCH_GAMER_TAG + " TEXT," +
                PAST_SEARCH_LAST_LOG_ON + " REAL" +
                ")";
        db.execSQL(CREATE_PAST_SEARCHES_TABLE);
    }

    // if upgrading, drop old tables and recreate them
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAST_SEARCHES);
            onCreate(db);
        }
    }

    // Add new past search
    public void addPastSearch(PastSearch pastSearch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAST_SEARCH_STEAM_ID, pastSearch.getSteamId());
        values.put(PAST_SEARCH_GAMER_TAG, pastSearch.getGamerTag());
        values.put(PAST_SEARCH_PROFILE_URL, pastSearch.getProfileUrl());
        values.put(PAST_SEARCH_LAST_LOG_ON, pastSearch.getLastLogOn());
        // insert row. If there is a conflict, replace the old past search
        db.insertWithOnConflict(TABLE_PAST_SEARCHES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Get all past searches
    public List<PastSearch> getAllPastSearches() {
        List<PastSearch> pastSearchList = new ArrayList<>();
        // select all query
        String selectAllQuery = "SELECT * FROM " + TABLE_PAST_SEARCHES;
        // open connection to database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllQuery, null);
        // loop all row and add to list
        if(cursor.moveToFirst()) {
            do {
                PastSearch pastSearch = new PastSearch(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getFloat(3));
                pastSearchList.add(pastSearch);
            } while (cursor.moveToNext());
        }
        return pastSearchList;
    }

    public void deletePastSearch(String uniqueSteamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = PAST_SEARCH_STEAM_ID + "=?";
        db.delete(TABLE_PAST_SEARCHES, whereClause, new String[] {uniqueSteamId});
    }

}
