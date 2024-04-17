package ch.swisso;

import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import kotlin.text.Charsets;

public class Event {

    private int id;
    private final String name;
    private final Date beginDate;
    private final Date endDate;
    private final int kind;
    private final String club;
    private final String map;
    private final String region;
    private final double koordn;
    private final double koorde;
    private final Date deadline;
    private final Uri ausschreibung;
    private final Uri weisungen;
    private final Uri anmeldung;
    private final Uri mutation;
    private final Uri startliste;
    private final Uri liveresultate;
    private final Uri rangliste;
    private final Uri teilnehmerliste;
    private boolean favorit;
    private List startlist;
    private List ranglist;
    private final UriArt selectedRanglistUri;
    private final UriArt selectedStartlistUri;

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
        selectedRanglistUri = rangliste == null && liveresultate != null ? UriArt.Liveresultate : UriArt.Rangliste;
        selectedStartlistUri = startliste == null && teilnehmerliste != null ? UriArt.Teilnehmerliste : UriArt.Startliste;
    }

    public void initLists(@NonNull Daten daten) {
        ArrayList<List> lists = daten.createListsByEvent(this);
        for (List l : lists) {
            switch (l.getListType()) {
                case Helper.ListType.START:
                    startlist = l;
                    break;
                case Helper.ListType.RANG:
                    ranglist = l;
                    break;
                case Helper.ListType.TEILNEHMER:
                    if (startlist == null)
                        startlist = l;
                    break;
                case Helper.ListType.LIVE:
                    if (ranglist == null)
                        ranglist = l;
                    break;
            }
        }
    }

    public int getRanglistTitle() {
        return rangliste == null && liveresultate != null ? R.string.liveresult : R.string.rangliste;
    }

    public int getStartlistTitle() {
        return startliste == null && teilnehmerliste != null ? R.string.teilnehmer : R.string.startlist;
    }

    public List getStartList() {
        return startlist;
    }

    public List getRangList() {
        return ranglist;
    }

    public Uri getSelectedUri(boolean isStartliste) {
        if (isStartliste)
            return getUri(selectedStartlistUri);
        else
            return getUri(selectedRanglistUri);
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

    public final boolean isFavorit() {
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
                        Log.e("SwissO", "Google Maps encoding failed", e);
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

    public String getDeeplinkUrl() {
        return "https://app.swiss-o.ch/event_details?event_id=" + id;
    }

    public boolean calNeedsUpdate(Cursor c, MyActivity act) {
        return !(name.equals(Helper.getString(c, Events.TITLE))
                && beginDate.getTime() == Helper.getLong(c, Events.DTSTART)
                && (endDate == null || (endDate.getTime() + 86400000) == Helper.getLong(c, Events.DTEND))
                && Helper.getInt(c, Events.ALL_DAY) == 1
                && (act.getString(R.string.open_in_swisso_app) + " " + getDeeplinkUrl()).equals(Helper.getString(c, Events.DESCRIPTION))
                && (map == null || map.equals(Helper.getString(c, Events.EVENT_LOCATION))));
    }

    public void toggleFavorit() {
        favorit = !favorit;
    }

    public enum Maps {Google, GoogleSat, Swisstopo, OSM}

    public enum UriArt {Details, Ausschreibung, Weisungen, Anmeldung, Mutation, Startliste, Rangliste, WKZ, Liveresultate, Teilnehmerliste, Kalender}
}
