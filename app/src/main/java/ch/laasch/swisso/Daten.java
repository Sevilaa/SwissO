package ch.laasch.swisso;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public int insertProfil(String vorname, String nachname, int si, String category) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_Vorname, vorname);
        daten.put(SQLiteHelper.COLUMN_Nachname, nachname);
        daten.put(SQLiteHelper.COLUMN_SI, si);
        daten.put(SQLiteHelper.COLUMN_Category, category);
        return (int) database.insert(SQLiteHelper.TABLE_Profil, null, daten);
    }

    public void updateProfil(int id, String vorname, String nachname, int si, String category) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_Vorname, vorname);
        daten.put(SQLiteHelper.COLUMN_Nachname, nachname);
        daten.put(SQLiteHelper.COLUMN_SI, si);
        daten.put(SQLiteHelper.COLUMN_Category, category);
        database.update(SQLiteHelper.TABLE_Profil, daten, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public Cursor getAllProfile() {
        return database.query(SQLiteHelper.TABLE_Profil, null, null, null, null, null, null);
    }

//    public Profil CreateProfil() {
//        Cursor cursor = getAllProfile();
//        if (cursor.isAfterLast()) {
//            insertProfil("", "", Helper.intnull, "");
//            cursor = getAllProfile();
//        }
//        return new Profil(cursor, this);
//    }

    //Table Freunde

    public int insertFreund(String vorname, int profilID) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, vorname);
        daten.put(SQLiteHelper.COLUMN_Profil, profilID);
        return (int) database.insert(SQLiteHelper.TABLE_Freunde, null, daten);
    }

    public Cursor getFreundeByProfil(int profilID) {
        return database.query(SQLiteHelper.TABLE_Freunde, null, SQLiteHelper.COLUMN_Profil + " = " + profilID, null, null, null, null);
    }

    public void deleteFreundById(int id) {
        database.delete(SQLiteHelper.TABLE_Freunde, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    //Table Clubs

    public int insertClub(String name, int profilID) {
        ContentValues daten = new ContentValues();
        daten.put(SQLiteHelper.COLUMN_NAME, name);
        daten.put(SQLiteHelper.COLUMN_Profil, profilID);
        return (int) database.insert(SQLiteHelper.TABLE_Clubs, null, daten);
    }

    public Cursor getClubsByProfil(int profilID) {
        return database.query(SQLiteHelper.TABLE_Clubs, null, SQLiteHelper.COLUMN_Profil + " = " + profilID, null, null, null, null);
    }

    public void deleteClubById(int id) {
        database.delete(SQLiteHelper.TABLE_Clubs, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    //Table Laeufer

//    public int insertLaeufer(String name, int jahrgang, String club, String cat,
//                             int startnummer, TimeSpan startzeit, TimeSpan zielzeit, int rang, Event e) {
//        ContentValues daten = new ContentValues();
//        daten.put(SQLiteHelper.COLUMN_Jahrgang, jahrgang);
//        daten.put(SQLiteHelper.COLUMN_NAME, name);
//        daten.put(SQLiteHelper.COLUMN_Club, club);
//        daten.put(SQLiteHelper.COLUMN_Category, cat);
//        daten.put(SQLiteHelper.COLUMN_Startnummer, startnummer);
//        daten.put(SQLiteHelper.COLUMN_Startzeit, startzeit.Ticks);
//        daten.put(SQLiteHelper.COLUMN_Zielzeit, zielzeit.Ticks);
//        daten.put(SQLiteHelper.COLUMN_Rang, rang);
//        daten.put(SQLiteHelper.COLUMN_Event, e.getId());
//        return (int) database.insert(SQLiteHelper.TABLE_Laeufer, null, daten);
//    }

    public Cursor getAllLaeuferByEvent(Event e, String filter, String order) {
        String where = SQLiteHelper.COLUMN_Event + " = " + e.getId() + " AND (" +
                SQLiteHelper.COLUMN_NAME + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_CLUB + " LIKE '%" + filter + "%')";
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where, null, null, null, order);
    }

    public Cursor getClubLaeuferByEvent(Event e, ArrayList<String> clubs, String filter, String order) {
        if (clubs.size() == 0) {
            return database.query(SQLiteHelper.TABLE_Laeufer, null, SQLiteHelper.COLUMN_CLUB + " = 'lkaasdfsjdf'", null, null, null, null);
        }
        StringBuilder where = new StringBuilder(SQLiteHelper.COLUMN_Event + " = " + e.getId() + " AND (" +
                SQLiteHelper.COLUMN_NAME + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_CLUB + " LIKE '%" + filter + "%') AND (" +
                SQLiteHelper.COLUMN_CLUB + " LIKE '%" + clubs.get(0) + "%'");
        for (int i = 1; i < clubs.size(); i++) {
            where.append(" OR " + SQLiteHelper.COLUMN_CLUB + " LIKE '%").append(clubs.get(i)).append("%'");
        }
        where.append(")");
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where.toString(), null, null, null, order);
    }

    public Cursor getFriendLaeuferByEvent(Event e, ArrayList<String> freunde, String filter, String order) {
        if (freunde.size() == 0) {
            return database.query(SQLiteHelper.TABLE_Laeufer, null, SQLiteHelper.COLUMN_CLUB + " = 'lkaasdfsjdf'", null, null, null, null);
        }
        StringBuilder where = new StringBuilder(SQLiteHelper.COLUMN_Event + " = " + e.getId() + " AND (" +
                SQLiteHelper.COLUMN_NAME + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_CLUB + " LIKE '%" + filter + "%') AND (" +
                SQLiteHelper.COLUMN_NAME + " LIKE '%" + freunde.get(0) + "%'");
        for (int i = 1; i < freunde.size(); i++) {
            where.append(" OR " + SQLiteHelper.COLUMN_NAME + " LIKE '%").append(freunde.get(i)).append("%'");
        }
        where.append(")");
        return database.query(SQLiteHelper.TABLE_Laeufer, null, where.toString(), null, null, null, order);
    }

    public int getLaeuferCountByEvent(Event e) {
        return getAllLaeuferByEvent(e, "", null).getCount();
    }

    public void deleteAllLaeuferByEvent(Event e) {
        database.delete(SQLiteHelper.TABLE_Laeufer, SQLiteHelper.COLUMN_Event + " = " + e.getId(), null);
    }

    //Table Events

    public int insertEvent(Event e) {
        return (int) database.insert(SQLiteHelper.TABLE_Events, null, CreateEventContentValues(e));
    }

    public void updateEvent(Event e) {
        database.update(SQLiteHelper.TABLE_Events, CreateEventContentValues(e), SQLiteHelper.COLUMN_ID + " = " + e.getId(), null);
    }

    private ContentValues CreateEventContentValues(Event e) {
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
        return database.query(SQLiteHelper.TABLE_Events, null, SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

    public Cursor getEvents() {
        return database.query(SQLiteHelper.TABLE_Events, null, null, null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

    public Cursor getEvents(String filter, HashMap<Chip, String> chips) {
        return database.query(SQLiteHelper.TABLE_Events, null, getFilterString(filter, chips), null, null, null, SQLiteHelper.COLUMN_BEGIN_DATE + " ASC;");
    }

  /*  public int getUpcomingEventId(String filter, HashMap<Chip, String> chips){
        long today = ;
        String where = "(" + getFilterString(filter, chips) + ") AND " + SQLiteHelper.COLUMN_Date + " >= " + today;
        Cursor c = database.query(SQLiteHelper.TABLE_Events, null, where, null, null, null, SQLiteHelper.COLUMN_Date + " ASC;");
        c.moveToFirst();
        return Helper.getInt(c, SQLiteHelper.COLUMN_ID);
    }*/

    private String getFilterString(String filter, HashMap<Chip, String> chips) {
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
//        boolean success = DateTime.TryParse(filter, out DateTime date);
//            if (success) {
//                where += " OR " + SQLiteHelper.COLUMN_Date + " = " + date.Ticks;
//            }
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

    //        private static String SameID(int id) {
//            return SQLiteHelper.COLUMN_ID + " = " + id;
//        }
//
    private String UriString(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
