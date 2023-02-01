package ch.swisso;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;

public class Daten {

    private final SQLiteHelper dbHelper;
    private SQLiteDatabase database;

    public Daten(Activity act) {
        dbHelper = new SQLiteHelper(act);
        Open();
    }

    public void Open() {
        database = dbHelper.getWritableDatabase();
    }

    public void Close() {
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

    public Cursor getLaeuferByEvent(int eventId) {
        String where = SQLiteHelper.COLUMN_EVENT + " = " + eventId;
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, null);
    }

    public Cursor getFilteredLaeuferByEvent(@NonNull Event e, ListFragment.ListType fragmentType, SingleListFragment.ListContent content, String filter, HashMap<Chip, String> chips, String order) {
        String where = SQLiteHelper.COLUMN_EVENT + " = " + e.getId();
        if (filter != null && !filter.trim().isEmpty()) {
            where += " AND (" + getFilterString(filter, chips) + ")";
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
                builder.append(col + " LIKE '%" + list.get(0) + "%'");
                for (int i = 1; i < list.size(); i++) {
                    builder.append(" OR " + col + " LIKE '%").append(list.get(i)).append("%'");
                }
                where += builder + ")";
            } else {
                return null;
            }
        }
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, order);
    }

    public int getLaeuferCountByEvent(Event e, ListFragment.ListType type) {
        return getFilteredLaeuferByEvent(e, type, SingleListFragment.ListContent.alle, null, null, null).getCount();
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

    public void deleteEvent(int id) {
        database.delete(SQLiteHelper.TABLE_Events, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public Cursor getEvents() {
        return database.query(SQLiteHelper.TABLE_Events, null, null, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

    public Cursor getEvents(String filter, HashMap<Chip, String> chips) {
        return database.query(SQLiteHelper.TABLE_Events, null, getFilterString(filter, chips), null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

    @Nullable
    private String getFilterString(String filter, @NonNull HashMap<Chip, String> chips) {
        StringBuilder builder = new StringBuilder();
        for (Chip chip : chips.keySet()) {
            if (chip.isChecked()) {
                builder.append(chips.get(chip)).append(" LIKE '%").append(filter).append("%' OR ");
            }
        }
        String where = builder.toString();
        if (where.isEmpty()) {
            return null;
        } else {
            return where.substring(0, where.length() - 4); // Remove last OR
        }
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
}
