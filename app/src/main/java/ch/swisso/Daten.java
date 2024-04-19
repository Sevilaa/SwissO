package ch.swisso;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Daten {

    private final SQLiteHelper dbHelper;
    private SQLiteDatabase database;

    private static final String[] eventSearchColumns = new String[]{SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_CLUB, SQLiteHelper.COLUMN_REGION, SQLiteHelper.COLUMN_MAP};
    private static final String[] laeuferSearchColumns = new String[]{SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_CLUB, SQLiteHelper.COLUMN_STARTNUMMER, SQLiteHelper.COLUMN_STARTZEIT, SQLiteHelper.COLUMN_KATEGORIE};

    public Daten(Activity act) {
        dbHelper = new SQLiteHelper(act);
        open();
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean isOpen() {
        return database.isOpen();
    }

    //Table Fruende, Clubs, Kats

    public static String getTable(@NonNull ProfilFragment.ProfilList list) {
        switch (list) {
            case Club:
                return SQLiteHelper.TABLE_Clubs;
            case Freund:
                return SQLiteHelper.TABLE_Freunde;
            case Kat:
            default:
                return SQLiteHelper.TABLE_Kats;
        }
    }

    public int insertProfilElement(@NonNull String name, ProfilFragment.ProfilList list) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, name.trim());
        return (int) database.insert(getTable(list), null, daten);
    }

    public Cursor getAllProfilElements(ProfilFragment.ProfilList list) {
        return database.query(getTable(list), null, null, null, null, null, null);
    }

    public void deleteProfilElementById(int id, ProfilFragment.ProfilList list) {
        database.delete(getTable(list), SQLiteHelper.COLUMN_AUTO_ID + " = " + id, null);
    }

    public ArrayList<String> getProfilListList(ProfilFragment.ProfilList type) {
        Cursor cursor = getAllProfilElements(type);
        cursor.moveToFirst();
        ArrayList<String> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            list.add(Helper.getString(cursor, SQLiteHelper.COLUMN_NAME));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    //Table Lists and Runners

    public void insertList(ContentValues contentValues) {
        database.insert(SQLiteHelper.TABLE_Lists, null, contentValues);
    }

    public void deleteList(int listID) {
        database.delete(SQLiteHelper.TABLE_Runners, SQLiteHelper.COLUMN_LIST + " = " + listID, null);
        database.delete(SQLiteHelper.TABLE_Lists, SQLiteHelper.COLUMN_ID + " = " + listID, null);
    }

    public ArrayList<List> createListsByEvent(@NonNull Event event) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_Lists, null, SQLiteHelper.COLUMN_EVENT + " = " + event.getId(), null, null, null, null);
        cursor.moveToFirst();
        ArrayList<List> lists = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            lists.add(new List(Helper.getInt(cursor, SQLiteHelper.COLUMN_ID), event, Helper.getInt(cursor, SQLiteHelper.COLUMN_LISTTYPE)));
            cursor.moveToNext();
        }
        cursor.close();
        return lists;
    }

    public void insertRunner(ContentValues contentValues) {
        database.insert(SQLiteHelper.TABLE_Runners, null, contentValues);
    }

    public boolean updateRunnersFromJson(String event_details) {
        database.beginTransaction();
        try {
            JSONObject event = new JSONObject(event_details);
            JSONArray lists = event.getJSONArray("lists");
            for (int i = 0; i < lists.length(); i++) {
                JSONObject list = lists.getJSONObject(i);
                int listID = list.getInt(SQLiteHelper.COLUMN_ID);
                deleteList(listID);
                ContentValues listValues = new ContentValues();
                listValues.put(SQLiteHelper.COLUMN_ID, listID);
                json2cvInt(list, listValues, SQLiteHelper.COLUMN_EVENT);
                json2cvInt(list, listValues, SQLiteHelper.COLUMN_LISTTYPE);
                insertList(listValues);
                JSONArray runners = list.getJSONArray("runners");
                for (int j = 0; j < runners.length(); j++) {
                    JSONObject runner = runners.getJSONObject(j);
                    ContentValues runnerValues = new ContentValues();
                    int id = runner.getInt(SQLiteHelper.COLUMN_ID);
                    runnerValues.put(SQLiteHelper.COLUMN_ID, id);
                    json2cvString(runner, runnerValues, SQLiteHelper.COLUMN_NAME);
                    json2cvString(runner, runnerValues, SQLiteHelper.COLUMN_CLUB);
                    json2cvString(runner, runnerValues, SQLiteHelper.COLUMN_KATEGORIE);
                    json2cvString(runner, runnerValues, SQLiteHelper.COLUMN_ORT);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_JAHRGANG);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_STARTNUMMER);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_STARTZEIT);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_ZIELZEIT);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_RANG);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_LIST);
                    json2cvInt(runner, runnerValues, SQLiteHelper.COLUMN_PROV);
                    insertRunner(runnerValues);
                }
            }
            database.setTransactionSuccessful();
        } catch (JSONException e) {
            Log.e("SwissO", "Laeufer Update failed", e);
            return false;
        } finally {
            database.endTransaction();
        }
        return true;
    }

    public Cursor getFilteredRunnersByList(int listId, String content, Pair<String, String> filter, String order) {
        String where = SQLiteHelper.COLUMN_LIST + " = " + listId;
        if (filter != null && filter.first != null && !filter.first.isEmpty()) {
            where += " AND (" + getFilterString(filter, laeuferSearchColumns) + ")";
        }
        if (content.equals(Helper.SingleListTab.tabFreunde) || content.equals(Helper.SingleListTab.tabClub)) {
            ArrayList<String> list;
            String col;
            if (content.equals(Helper.SingleListTab.tabFreunde)) {
                list = getProfilListList(ProfilFragment.ProfilList.Freund);
                col = SQLiteHelper.COLUMN_NAME;
            } else {
                list = getProfilListList(ProfilFragment.ProfilList.Club);
                col = SQLiteHelper.COLUMN_CLUB;
            }
            if (!list.isEmpty()) {
                StringBuilder builder = new StringBuilder(" AND (");
                builder.append(col).append(" LIKE '%").append(list.get(0)).append("%'");
                for (int i = 1; i < list.size(); i++) {
                    builder.append(" OR ").append(col).append(" LIKE '%").append(list.get(i)).append("%'");
                }
                where += builder + ")";
            } else {
                return null;
            }
        } else if (!content.equals(Helper.SingleListTab.tabAlle)) {
            where += " AND " + SQLiteHelper.COLUMN_KATEGORIE + " = '" + content + "'";
        }
        return database.query(SQLiteHelper.TABLE_Runners, null, where, null, null, null, order);
    }

    public HashMap<String, String> getLaeuferSeachSuggestions(String search, int listId) {
        HashMap<String, String> results = new HashMap<>();
        for (String column : laeuferSearchColumns) {
            Cursor c = database.query(true, SQLiteHelper.TABLE_Runners, new String[]{column}, column + " LIKE '%" + search + "%' AND " + SQLiteHelper.COLUMN_LIST + " = " + listId, null, null, null, column, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                results.put(c.getString(0), column);
                c.moveToNext();
            }
            c.close();
        }
        return results;
    }

    //Table Events

    public void insertEvent(ContentValues values) {
        database.insert(SQLiteHelper.TABLE_Events, null, values);
    }

    public void updateEvent(ContentValues values, int id) {
        database.update(SQLiteHelper.TABLE_Events, values, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public boolean updateEventsFromJson(String json) {
        try {
            database.beginTransaction();
            JSONArray array = new JSONArray(json);
            Cursor d = getEvents();
            ArrayList<Integer> ids = new ArrayList<>();
            d.moveToFirst();
            while (!d.isAfterLast()) {
                ids.add(Helper.getInt(d, SQLiteHelper.COLUMN_ID));
                d.moveToNext();
            }
            d.close();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonEvent = array.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                int id = jsonEvent.getInt(SQLiteHelper.COLUMN_ID);
                contentValues.put(SQLiteHelper.COLUMN_ID, id);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_NAME);
                json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_BEGIN_DATE);
                json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_END_DATE);
                json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_DEADLINE);
                json2cvInt(jsonEvent, contentValues, SQLiteHelper.COLUMN_KIND);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_REGION);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_CLUB);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_MAP);
                json2cvDouble(jsonEvent, contentValues, SQLiteHelper.COLUMN_INT_NORD);
                json2cvDouble(jsonEvent, contentValues, SQLiteHelper.COLUMN_INT_EAST);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_AUSSCHREIBUNG);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_WEISUNGEN);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_LIVE_RESULTATE);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_STARTLISTE);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_ANMELDUNG);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_MUTATION);
                json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_TEILNEHMERLISTE);
                if (ids.contains(id)) {
                    updateEvent(contentValues, id);
                    ids.remove((Integer) id);
                } else {
                    contentValues.put(SQLiteHelper.COLUMN_FAVORIT, 0);
                    insertEvent(contentValues);
                }
            }
            for (int id : ids) {
                deleteEvent(id);
            }
            database.setTransactionSuccessful();
        } catch (JSONException e) {
            Log.e("SwissO", "Event Update failed", e);
            return false;
        } finally {
            database.endTransaction();
        }
        return true;
    }

    public void deleteEvent(int id) {
        database.delete(SQLiteHelper.TABLE_Events, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public Cursor getEvents() {
        return database.query(SQLiteHelper.TABLE_Events, null, null, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

    public Event createEventById(int id) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_Events, null, SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Event event = new Event(cursor);
        cursor.close();
        return event;
    }

    public Cursor getEvents(Pair<String, String> filter, boolean onlyFavs) {
        if (filter != null && filter.first != null && !filter.first.isEmpty()) {
            return database.query(SQLiteHelper.TABLE_Events, null, "(" + getFilterString(filter, eventSearchColumns) + ")" + (onlyFavs ? " AND " + SQLiteHelper.COLUMN_FAVORIT + " = 1" : ""), null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
        } else {
            return database.query(SQLiteHelper.TABLE_Events, null, onlyFavs ? SQLiteHelper.COLUMN_FAVORIT + " = 1" : null, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
        }
    }

    @NonNull
    private String getFilterString(@NonNull Pair<String, String> filter, @NonNull String[] columns) {
        if (filter.second != null) {
            return filter.second + " LIKE '%" + filter.first + "%'";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(columns[0]).append(" LIKE '%").append(filter.first).append("%'");
            for (int i = 1; i < columns.length; i++) {
                builder.append(" OR ").append(columns[i]).append(" LIKE '%").append(filter.first).append("%'");
            }
            return builder.toString();
        }
    }

    public HashMap<String, String> getEventSeachSuggestions(String search, boolean fav) {
        HashMap<String, String> results = new HashMap<>();
        for (String column : eventSearchColumns) {
            Cursor c = database.query(true, SQLiteHelper.TABLE_Events, new String[]{column}, column + " LIKE '%" + search + "%'" + (fav ? " AND " + SQLiteHelper.COLUMN_FAVORIT + " = 1" : ""), null, null, null, column, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                results.put(c.getString(0), column);
                c.moveToNext();
            }
            c.close();
        }
        return results;
    }

    //Table Messages

    public Cursor getMessages() {
        return database.query(SQLiteHelper.TABLE_Messages, null, null, null, null, null, null);
    }

    public void insertMessage(ContentValues contentValues) {
        database.insert(SQLiteHelper.TABLE_Messages, null, contentValues);
    }

    public void deleteMessage(int id) {
        database.delete(SQLiteHelper.TABLE_Messages, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public Cursor getUnreadMessages() {
        return database.query(SQLiteHelper.TABLE_Messages, null, SQLiteHelper.COLUMN_VIEWED + " = " + 0, null, null, null, null);
    }

    public void updateAsRead() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelper.COLUMN_VIEWED, 1);
        database.update(SQLiteHelper.TABLE_Messages, contentValues, null, null);
    }

    // Json to ContentValues functions

    private static void json2cvInt(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getInt(field));
        }
    }

    private static void json2cvLong(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getLong(field));
        }
    }

    private static void json2cvDouble(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getDouble(field));
        }
    }

    private static void json2cvString(@NonNull JSONObject json, @NonNull ContentValues values, String field) throws JSONException {
        if (!json.isNull(field)) {
            String s = json.getString(field).trim();
            if (!s.isEmpty()) {
                values.put(field, s);
                return;
            }
        }
        values.put(field, (String) null);
    }
}
