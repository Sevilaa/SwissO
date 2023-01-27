package ch.swisso;

import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Event {


    private int id;
    private String name;
    private Date beginDate;
    private Date endDate;
    private int kind;
    private String club;
    private String map;
    private String region;
    private double koordn;
    private double koorde;
    private Date deadline;
    private Uri ausschreibung;
    private Uri weisungen;
    private Uri anmeldung;
    private Uri mutation;
    private Uri startliste;
    private Uri liveresultate;
    private Uri rangliste;
    private Uri teilnehmerliste;

    public Event(Cursor cursor) {
        id = Helper.getInt(cursor, SQLiteHelper.COLUMN_ID);
        name = Helper.getString(cursor, SQLiteHelper.COLUMN_NAME);
        beginDate = Helper.getDate(cursor, SQLiteHelper.COLUMN_BEGIN_DATE);
        endDate = Helper.getDate(cursor, SQLiteHelper.COLUMN_END_DATE);
        deadline = Helper.getDate(cursor, SQLiteHelper.COLUMN_DEADLINE);
        kind = Helper.getInt(cursor, SQLiteHelper.COLUMN_KIND);
        region = Helper.getString(cursor, SQLiteHelper.COLUMN_REGION);
        club = Helper.getString(cursor, SQLiteHelper.COLUMN_CLUB);
        map = Helper.getString(cursor, SQLiteHelper.COLUMN_MAP);
        koordn = Helper.getDouble(cursor, SQLiteHelper.COLUMN_INT_NORD);
        koorde = Helper.getDouble(cursor, SQLiteHelper.COLUMN_INT_EAST);
        ausschreibung = Helper.getUri(cursor, SQLiteHelper.COLUMN_AUSSCHREIBUNG);
        weisungen = Helper.getUri(cursor, SQLiteHelper.COLUMN_WEISUNGEN);
        rangliste = Helper.getUri(cursor, SQLiteHelper.COLUMN_RANGLISTE);
        liveresultate = Helper.getUri(cursor, SQLiteHelper.COLUMN_LIVE_RESULTATE);
        startliste = Helper.getUri(cursor, SQLiteHelper.COLUMN_STARTLISTE);
        anmeldung = Helper.getUri(cursor, SQLiteHelper.COLUMN_ANMELDUNG);
        mutation = Helper.getUri(cursor, SQLiteHelper.COLUMN_MUTATION);
        teilnehmerliste = Helper.getUri(cursor, SQLiteHelper.COLUMN_TEILNEHMERLISTE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Event) {
            return ((Event) o).getId() == id;
        }
        return false;
    }

    public final int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final Date getBeginDate() {
        return beginDate;
    }

    public final Date getEndDate() {
        return endDate;
    }

    public final String getClub() {
        return club;
    }

    public final String getMap() {
        return map;
    }

    public final String getRegion() {
        return region;
    }

    public final double getKoordn() {
        return koordn;
    }

    public final double getKoorde() {
        return koorde;
    }

    public final Date getDeadline() {
        return deadline;
    }

    public Uri getUri(@NonNull UriArt uriArt) {
        switch (uriArt) {
            case Ausschreibung:
                return ausschreibung;
            case Weisungen:
                return weisungen;
            case Anmeldung:
                return anmeldung;
            case Mutation:
                return mutation;
            case Startliste:
                return startliste;
            case Rangliste:
                return rangliste;
            case WKZ:
                if (koordn != Helper.intnull && koorde != Helper.intnull) {
                    return Uri.parse("geo:" + koordn + "," + koorde + "?q=" + koordn + "," + koorde + "(WKZ)");
                }
                return null;
            case Liveresultate:
                return liveresultate;
            case Teilnehmerliste:
                return teilnehmerliste;
            case Kalender:
                return Uri.parse("https://swisso.ch"); //Dummy URI to not return null
        }
        return null;
    }

    public enum UriArt {Ausschreibung, Weisungen, Anmeldung, Mutation, Startliste, Rangliste, WKZ, Liveresultate, Teilnehmerliste, Kalender}
}
