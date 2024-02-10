package ch.swisso;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        Open();
    }

    public void Open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //Table Freunde

    public int insertFreund(String name) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, name);
        return (int) database.insert(SQLiteHelper.TABLE_Freunde, null, daten);
    }

    public Cursor getAllFreunde() {
        return database.query(SQLiteHelper.TABLE_Freunde, null, null, null, null, null, null);
    }

    public void deleteFreundById(int id) {
        database.delete(SQLiteHelper.TABLE_Freunde, SQLiteHelper.COLUMN_AUTO_ID + " = " + id, null);
    }

    //Table Clubs

    public int insertClub(String name) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, name);
        return (int) database.insert(SQLiteHelper.TABLE_Clubs, null, daten);
    }

    public Cursor getAllClubs() {
        return database.query(SQLiteHelper.TABLE_Clubs, null, null, null, null, null, null);
    }

    public void deleteClubById(int id) {
        database.delete(SQLiteHelper.TABLE_Clubs, SQLiteHelper.COLUMN_AUTO_ID + " = " + id, null);
    }

    public ArrayList<String> getFriendsClubList(boolean club) {
        Cursor cursor = club ? getAllClubs() : getAllFreunde();
        cursor.moveToFirst();
        ArrayList<String> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            list.add(Helper.getString(cursor, SQLiteHelper.COLUMN_NAME));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    //Table Laeufer

    public void insertLaeufer(ContentValues contentValues) {
        database.insert(SQLiteHelper.TABLE_Laeufer, null, contentValues);
    }

    public void updateLaeufer(ContentValues contentValues, int id) {
        database.update(SQLiteHelper.TABLE_Laeufer, contentValues, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public boolean updateLaeuferFromJson(String json, int eventId) {
        database.beginTransaction();
        try {
            JSONArray array = new JSONArray(json);
            Cursor c = getLaeuferByEvent(eventId);
            ArrayList<Integer> ids = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                ids.add(Helper.getInt(c, SQLiteHelper.COLUMN_ID));
                c.moveToNext();
            }
            c.close();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonLauefer = array.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                int id = jsonLauefer.getInt(SQLiteHelper.COLUMN_ID);
                contentValues.put(SQLiteHelper.COLUMN_ID, id);
                json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_NAME);
                json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_CLUB);
                json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_KATEGORIE);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_JAHRGANG);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTNUMMER);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTZEIT);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_ZIELZEIT);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANG);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_EVENT);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTLISTE);
                json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                if (ids.contains(id)) {
                    updateLaeufer(contentValues, id);
                    ids.remove((Integer) id);
                } else {
                    insertLaeufer(contentValues);
                }
            }
            for (int id : ids) {
                deleteLaeuferById(id);
            }
            database.setTransactionSuccessful();
        } catch (JSONException e) {
            Log.e("SwissO", e != null ? e.getMessage() : e.toString());
            return false;
        } finally {
            database.endTransaction();
        }
        return true;
    }

    public Cursor getLaeuferByEvent(int eventId) {
        String where = SQLiteHelper.COLUMN_EVENT + " = " + eventId;
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, null);
    }

    public boolean isProvListe(int eventId, boolean startliste) {
        String[] column = new String[]{startliste ? SQLiteHelper.COLUMN_STARTLISTE : SQLiteHelper.COLUMN_RANGLISTE};
        String where = SQLiteHelper.COLUMN_EVENT + " = " + eventId + " AND " + column[0] + " = ";
        Cursor dev = database.query(true, SQLiteHelper.TABLE_Laeufer, column, where + Helper.ZeitStatus.DEFINITIV, null, null, null, null, null);
        Cursor prov = database.query(true, SQLiteHelper.TABLE_Laeufer, column, where + Helper.ZeitStatus.PROV, null, null, null, null, null);
        boolean isLive = dev.getCount() == 0 && prov.getCount() > 0;
        dev.close();
        prov.close();
        return isLive;
    }

    public Cursor getFilteredLaeuferByEvent(int eventID, ListFragment.ListType fragmentType, SingleListFragment.ListContent content, String filter, String order) {
        String where = SQLiteHelper.COLUMN_EVENT + " = " + eventID;
        if (filter != null && !filter.trim().isEmpty()) {
            where += " AND (" + getFilterString(filter, laeuferSearchColumns) + ")";
        }
        if (fragmentType == ListFragment.ListType.Rangliste) {
            where += " AND " + SQLiteHelper.COLUMN_RANGLISTE + " > 0";
        } else if (fragmentType == ListFragment.ListType.Startliste) {
            where += " AND " + SQLiteHelper.COLUMN_STARTLISTE + " > 0";
        }
        if (content != SingleListFragment.ListContent.alle) {
            ArrayList<String> list;
            String col;
            if (content == SingleListFragment.ListContent.Friends) {
                list = getFriendsClubList(false);
                col = SQLiteHelper.COLUMN_NAME;
            } else {
                list = getFriendsClubList(true);
                col = SQLiteHelper.COLUMN_CLUB;
            }
            if (list.size() > 0) {
                StringBuilder builder = new StringBuilder(" AND (");
                builder.append(col).append(" LIKE '%").append(list.get(0)).append("%'");
                for (int i = 1; i < list.size(); i++) {
                    builder.append(" OR ").append(col).append(" LIKE '%").append(list.get(i)).append("%'");
                }
                where += builder + ")";
            } else {
                return null;
            }
        }
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, order);
    }

    public HashMap<String, String> getLaeuferSeachSuggestions(String search, int eventID, SingleListFragment.ListContent listContent) { //TODO only list content in suggestions
        HashMap<String, String> results = new HashMap<>();
        for (String column : laeuferSearchColumns) {
            Cursor c = database.query(true, SQLiteHelper.TABLE_Laeufer, new String[]{column}, column + " LIKE '%" + search + "%' AND " + SQLiteHelper.COLUMN_EVENT + " = " + eventID, null, null, null, column, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                results.put(c.getString(0), column);
                c.moveToNext();
            }
            c.close();
        }
        return results;
    }

    public int getLaeuferCountByEvent(int eventID, ListFragment.ListType type) {
        return getFilteredLaeuferByEvent(eventID, type, SingleListFragment.ListContent.alle, null, null).getCount();
    }

    public void deleteLaeuferById(int id) {
        database.delete(SQLiteHelper.TABLE_Laeufer, SQLiteHelper.COLUMN_ID + " = " + id, null);
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
            Log.e("SwissO", e != null ? e.getMessage() : e.toString());
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
        return new Event(cursor);
    }

    public Cursor getEvents(String filter, boolean onlyFavs) {
        if (filter != null && !filter.isEmpty()) {
            return database.query(SQLiteHelper.TABLE_Events, null, "(" + getFilterString(filter, eventSearchColumns) + ")" + (onlyFavs ? " AND " + SQLiteHelper.COLUMN_FAVORIT + " = 1" : ""), null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
        } else {
            return database.query(SQLiteHelper.TABLE_Events, null, onlyFavs ? SQLiteHelper.COLUMN_FAVORIT + " = 1" : null, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
        }
    }

    @NonNull
    private String getFilterString(String filter, @NonNull String[] columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(columns[0]).append(" LIKE '%").append(filter).append("%'");
        for (int i = 1; i < columns.length; i++) {
            builder.append(" OR ").append(columns[i]).append(" LIKE '%").append(filter).append("%'");
        }
        return builder.toString();
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
            if (!s.equals("")) {
                values.put(field, s);
                return;
            }
        }
        values.put(field, (String) null);
    }
}
