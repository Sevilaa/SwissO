package ch.swisso;

import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateUtils;

public class Laeufer {

    //public Laeufer(Event e, string name, string jahrgang, string club, string category, string startnummer, string starttime, string rang, string zielzeit) {
    //    this.category = category;
    //    this.name = name;
    //    this.starttime = starttime;
    //    this.club = club;
    //    this.zielzeit = zielzeit;
    //    eventt = e;
    //    this.jahrgang = jahrgang;
    //    this.startnummer = startnummer;
    //    this.rang = rang;

    //}

    public Laeufer(Cursor cursor) {
        id = Helper.getInt(cursor, SQLiteHelper.COLUMN_ID);
        name = Helper.getString(cursor, SQLiteHelper.COLUMN_NAME);
        jahrgang = Helper.getInt(cursor, SQLiteHelper.COLUMN_JAHRGANG);
        club = Helper.getString(cursor, SQLiteHelper.COLUMN_CLUB);
        category = Helper.getString(cursor, SQLiteHelper.COLUMN_KATEGORIE);
        startnummer = Helper.getInt(cursor, SQLiteHelper.COLUMN_STARTNUMMER);
        startzeit = Helper.getInt(cursor, SQLiteHelper.COLUMN_STARTZEIT);
        zielzeit = Helper.getInt(cursor, SQLiteHelper.COLUMN_ZIELZEIT);
        rang = Helper.getInt(cursor, SQLiteHelper.COLUMN_RANG);
    }

    private int id;
    private String name;
    private int jahrgang;
    private String club;
    private String category;
    private int startnummer;
    private int startzeit;
    private int zielzeit;
    private int rang;

    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final int getJahrgang() {
        return jahrgang;
    }

    public final String getClub() {
        return club;
    }

    public final String getCategory() {
        return category;
    }

    public final int getStartnummer() {
        return startnummer;
    }

    public int getStartZeit() {
        return startzeit;
    }

    public int getZielzeit() {
        return zielzeit;
    }

    public final int getRang() {
        return rang;
    }

    public String getRangString(Resources res) {
        if (rang == Helper.Disqet.AUSSER_KONKURENZ)
            return res.getString(R.string.aK);
        if (rang == Helper.intnull)
            return "";
        return rang + ".";
    }

    public String getZielzeitString(Resources res) {
        if (zielzeit == Helper.Disqet.POSTEN_FALSCH)
            return res.getString(R.string.postenfalsch);
        if (zielzeit == Helper.Disqet.DNS)
            return res.getString(R.string.dns);
        if (zielzeit == Helper.Disqet.DISQET)
            return res.getString(R.string.disqet);
        if (zielzeit == Helper.Disqet.POSTEN_FEHLT)
            return res.getString(R.string.postenfehlt);
        if (zielzeit == Helper.Disqet.AUFGEGEBEN)
            return res.getString(R.string.aufgegeben);
        if (zielzeit == Helper.Disqet.NICHT_KLASSIERT)
            return res.getString(R.string.nichtklassiert);
        if (zielzeit == Helper.Disqet.UEBERZEIT)
            return res.getString(R.string.ueberzeit);
        return DateUtils.formatElapsedTime(zielzeit / 1000);
    }
}

