package ch.swisso;

import android.database.Cursor;
import android.net.Uri;

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

//    public Event(String title, DateTime date, String club, String map, String region, double koordn, double koorde, DateTime deadline,
//                 String ausschreibung, String weisungen, String anmeldung, String mutation, String startliste, String liveresultate, String rangliste, int portal) {
//        Title = title;
//        Date = date;
//        Club = club;
//        Map = map;
//        Region = region;
//        Koorde = koorde;
//        Koordn = koordn;
//        Deadline = deadline;
//        Ausschreibung = NewUri(ausschreibung);
//        Weisungen = NewUri(weisungen);
//        Anmeldung = NewUri(anmeldung);
//        Mutation = NewUri(mutation);
//        Startliste = NewUri(startliste);
//        Liveresultate = NewUri(liveresultate);
//        Rangliste = NewUri(rangliste);
//        Eventportal = portal;
//    }

    public Event(JSONObject json) {
        try {
            id = json.getInt(SQLiteHelper.COLUMN_ID);
            name = Helper.getString(json, SQLiteHelper.COLUMN_NAME);
            beginDate = Helper.getDate(json, SQLiteHelper.COLUMN_BEGIN_DATE);
            endDate = Helper.getDate(json, SQLiteHelper.COLUMN_END_DATE);
            deadline = Helper.getDate(json, SQLiteHelper.COLUMN_DEADLINE);
            kind = json.getInt(SQLiteHelper.COLUMN_KIND);
            region = Helper.getString(json, SQLiteHelper.COLUMN_REGION);
            club = Helper.getString(json, SQLiteHelper.COLUMN_CLUB);
            map = Helper.getString(json, SQLiteHelper.COLUMN_MAP);
            koordn = json.isNull(SQLiteHelper.COLUMN_INT_NORD) ? Helper.intnull : json.getDouble(SQLiteHelper.COLUMN_INT_NORD);
            koorde = json.isNull(SQLiteHelper.COLUMN_INT_EAST) ? Helper.intnull : json.getDouble(SQLiteHelper.COLUMN_INT_EAST);
            ausschreibung = Helper.getUri(json, SQLiteHelper.COLUMN_AUSSCHREIBUNG);
            weisungen = Helper.getUri(json, SQLiteHelper.COLUMN_WEISUNGEN);
            rangliste = Helper.getUri(json, SQLiteHelper.COLUMN_RANGLISTE);
            liveresultate = Helper.getUri(json, SQLiteHelper.COLUMN_LIVE_RESULTATE);
            startliste = Helper.getUri(json, SQLiteHelper.COLUMN_STARTLISTE);
            anmeldung = Helper.getUri(json, SQLiteHelper.COLUMN_ANMELDUNG);
            mutation = Helper.getUri(json, SQLiteHelper.COLUMN_MUTATION);
            teilnehmerliste = Helper.getUri(json, SQLiteHelper.COLUMN_TEILNEHMERLISTE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

//    private void setName(String value) {
//        name = value;
//    }

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

    public Uri getUri(UriArt uriArt) {
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
        }
        return null;
    }

    /*public final int getEventportal() {
        return eventportal;
    }*/

//    public void Merge(Event e) {
//        Region ??=e.Region;
//        Club ??=e.Club;
//        Map ??=e.Map;
//        Deadline = Deadline == DateTime.MinValue ? e.Deadline : Deadline;
//        Koorde = Koorde == Helper.intnull ? e.Koorde : Koorde;
//        Koordn = Koordn == Helper.intnull ? e.Koordn : Koordn;
//        Ausschreibung ??=e.Ausschreibung;
//        Weisungen ??=e.Weisungen;
//        Rangliste ??=e.Rangliste;
//        Liveresultate ??=e.Liveresultate;
//        Startliste ??=e.Startliste;
//        Anmeldung ??=e.Anmeldung;
//        Mutation ??=e.Mutation;
//    }

//    public bool Equals(Event e) {
//        bool b = e.Date.Ticks == Date.Ticks;
//        b = b && e.Eventportal == Eventportal;
//        if(Club != null && e.Club != null) {
//            b = b && Club == e.Club;
//        }
//        if(Title.Length >= 5) {
//            b = b && ((Title[0] == e.Title[0] && Title[1] == e.Title[1]) || (Title[0] == e.Title[4] && Title[1] == e.Title[5]) || (Title[4] == e.Title[0] && Title[5] == e.Title[1]));
//        }
//        else {
//            b = b && (Title[0] == e.Title[0] && Title[1] == e.Title[1]);
//        }
//        return b;
//    }

    public enum UriArt {Ausschreibung, Weisungen, Anmeldung, Mutation, Startliste, Rangliste, WKZ, Liveresultate, Teilnehmerliste}
}
