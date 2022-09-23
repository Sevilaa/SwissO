package ch.swisso;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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

    //Table Profil

    /*public int insertProfil(String vorname, String nachname, int si, String category) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_Vorname, vorname);
        daten.put(SQLiteHelper.COLUMN_Nachname, nachname);
        daten.put(SQLiteHelper.COLUMN_SI, si);
        daten.put(SQLiteHelper.COLUMN_KATEGORIE, category);
        return (int) database.insert(SQLiteHelper.TABLE_Profil, null, daten);
    }

    public void updateProfil(int id, String vorname, String nachname, int si, String category) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_Vorname, vorname);
        daten.put(SQLiteHelper.COLUMN_Nachname, nachname);
        daten.put(SQLiteHelper.COLUMN_SI, si);
        daten.put(SQLiteHelper.COLUMN_KATEGORIE, category);
        database.update(SQLiteHelper.TABLE_Profil, daten, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public Cursor getAllProfile() {
        return database.query(SQLiteHelper.TABLE_Profil, null, null, null, null, null, null);
    }

    public Profil CreateProfil() {
        Cursor cursor = getAllProfile();
        if (cursor.isAfterLast()) {
            insertProfil("", "", Helper.intnull, "");
            cursor = getAllProfile();
        }
        return new Profil(cursor, this);
    }*/

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

    public Cursor getLaeuferById(int id) {
        String where = SQLiteHelper.COLUMN_ID + " = " + id;
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, null);
    }

    public Cursor getFilteredLaeuferByEvent(@NonNull Event e, MainActivity.FragmentType fragmentType, SingleListFragment.ListContent content, String filter, HashMap<Chip, String> chips, String order) {
        String where = SQLiteHelper.COLUMN_EVENT + " = " + e.getId();
        if (filter != null && !filter.trim().isEmpty()) {
            where += " AND (" + getFilterString(filter, chips) + ")";
        }
        if (fragmentType == MainActivity.FragmentType.Rangliste) {
            where += " AND " + SQLiteHelper.COLUMN_RANGLISTE + " > 0";
        } else if (fragmentType == MainActivity.FragmentType.Startliste) {
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

    public int getLaeuferCountByEvent(Event e, MainActivity.FragmentType type) {
        return getFilteredLaeuferByEvent(e, type, SingleListFragment.ListContent.alle, null, null, null).getCount();
    }

    //Table Events

    public void insertEvent(Event e) {
        database.insert(SQLiteHelper.TABLE_Events, null, CreateEventContentValues(e));
    }

    public void updateEvent(Event e) {
        database.update(SQLiteHelper.TABLE_Events, CreateEventContentValues(e), SQLiteHelper.COLUMN_ID + " = " + e.getId(), null);
    }

    @NonNull
    private ContentValues CreateEventContentValues(@NonNull Event e) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, e.getName());
        if (e.getBeginDate() != null)
            daten.put(SQLiteHelper.COLUMN_BEGIN_DATE, e.getBeginDate().getTime());
        if (e.getEndDate() != null)
            daten.put(SQLiteHelper.COLUMN_END_DATE, e.getEndDate().getTime());
        if (e.getDeadline() != null)
            daten.put(SQLiteHelper.COLUMN_DEADLINE, e.getDeadline().getTime());
        daten.put(SQLiteHelper.COLUMN_REGION, e.getRegion());
        daten.put(SQLiteHelper.COLUMN_CLUB, e.getClub());
        daten.put(SQLiteHelper.COLUMN_MAP, e.getMap());
        daten.put(SQLiteHelper.COLUMN_INT_NORD, e.getKoordn());
        daten.put(SQLiteHelper.COLUMN_INT_EAST, e.getKoorde());
        daten.put(SQLiteHelper.COLUMN_AUSSCHREIBUNG, UriString(e.getUri(Event.UriArt.Ausschreibung)));
        daten.put(SQLiteHelper.COLUMN_WEISUNGEN, UriString(e.getUri(Event.UriArt.Weisungen)));
        daten.put(SQLiteHelper.COLUMN_RANGLISTE, UriString(e.getUri(Event.UriArt.Rangliste)));
        daten.put(SQLiteHelper.COLUMN_ANMELDUNG, UriString(e.getUri(Event.UriArt.Anmeldung)));
        daten.put(SQLiteHelper.COLUMN_MUTATION, UriString(e.getUri(Event.UriArt.Mutation)));
        daten.put(SQLiteHelper.COLUMN_LIVE_RESULTATE, UriString(e.getUri(Event.UriArt.Liveresultate)));
        daten.put(SQLiteHelper.COLUMN_STARTLISTE, UriString(e.getUri(Event.UriArt.Startliste)));
        daten.put(SQLiteHelper.COLUMN_TEILNEHMERLISTE, UriString(e.getUri(Event.UriArt.Teilnehmerliste)));
        return daten;
    }

    public Cursor getEventById(int id) {
        return database.query(SQLiteHelper.TABLE_Events, null, SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
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

//    public Cursor getTodayEvent() {
//        Date date = new Date(System.currentTimeMillis());
//        Calendar calendar = Calendar.getInstance();
//        calendar.clear(Calendar.HOUR);
//        calendar.clear(Calendar.MINUTE);
//        calendar.clear(Calendar.SECOND);
//        calendar.clear(Calendar.MILLISECOND); //TODO
//        return database.query(SQLiteHelper.TABLE_Events, null, SQLiteHelper.COLUMN_Date + " = " + today, null, null, null, SQLiteHelper.COLUMN_Date + " ASC;");
//    }

    private String UriString(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
