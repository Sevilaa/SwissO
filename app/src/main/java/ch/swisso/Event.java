package ch.swisso;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import kotlin.text.Charsets;

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
    private boolean favorit;

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
        favorit = Helper.getBool(cursor, SQLiteHelper.COLUMN_FAVORIT);
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

    public final boolean isFavorit(){
        return favorit;
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
                    return Uri.parse("geo:" + koordn + "," + koorde + "?q=" + koordn + "," + koorde + "(WKZ " + name + ")");
                }
                return null;
            case Liveresultate:
                return liveresultate;
            case Teilnehmerliste:
                return teilnehmerliste;
            case Kalender:
                return Uri.parse("https://swiss-o.ch"); //Dummy URI to not return null
        }
        return null;
    }

    public String getMapsUrl(@NonNull Maps maps) {
        if (koorde != Helper.intnull) {
            switch (maps) {
                case Google:
                    try {
                        return "https://maps.google.com/maps?q=" + koordn + "," + koorde + URLEncoder.encode("(WKZ " + name + ")", Charsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        Log.e("SwissO", e.toString());
                        return "https://maps.google.com/maps?q=" + koordn + "," + koorde + "(WKZ " + name + ")";
                    }
                case GoogleSat:
                    return getMapsUrl(Maps.Google) + "&t=h";
                case Swisstopo:
                    return "https://test.map.geo.admin.ch/?lon=" + koorde + "&lat=" + koordn + "&zoom=8&crosshair=marker";
                case OSM:
                    return "http://www.openstreetmap.org/?mlat=" + koordn + "&mlon=" + koorde + "#map=12/" + koordn + "/" + koorde;
            }
        }
        return null;
    }

    public String getCalenderLocation(Maps maps) {
        String desc = "";
        if (map != null) {
            desc += map + " ";
        }
        String mapsURL = getMapsUrl(maps);
        if (mapsURL != null) {
            desc += getMapsUrl(maps);
        }
        return desc;
    }

    public void toggleFavorit(){
        favorit = !favorit;
    }

    public enum Maps {Google, GoogleSat, Swisstopo, OSM}

    public enum UriArt {Details, Ausschreibung, Weisungen, Anmeldung, Mutation, Startliste, Rangliste, WKZ, Liveresultate, Teilnehmerliste, Kalender}
}
