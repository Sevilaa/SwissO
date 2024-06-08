package ch.swisso;

import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateUtils;

public class Runner {

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

    public Runner(Cursor cursor) {
        id = Helper.getInt(cursor, SQLiteHelper.COLUMN_ID);
        name = Helper.getString(cursor, SQLiteHelper.COLUMN_NAME);
        year = Helper.getInt(cursor, SQLiteHelper.COLUMN_JAHRGANG);
        club = Helper.getString(cursor, SQLiteHelper.COLUMN_CLUB);
        category = Helper.getString(cursor, SQLiteHelper.COLUMN_KATEGORIE);
        startNumber = Helper.getInt(cursor, SQLiteHelper.COLUMN_STARTNUMMER);
        startTime = Helper.getInt(cursor, SQLiteHelper.COLUMN_STARTZEIT);
        finishTime = Helper.getInt(cursor, SQLiteHelper.COLUMN_ZIELZEIT);
        rank = Helper.getInt(cursor, SQLiteHelper.COLUMN_RANG);
        location = Helper.getString(cursor, SQLiteHelper.COLUMN_ORT);
    }

    private int id;
    private String name;
    private int year;
    private String club;
    private String category;
    private int startNumber;
    private int startTime;
    private int finishTime;
    private int rank;
    private String location;

    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final int getYear() {
        return year;
    }

    public final String getClub() {
        return club;
    }

    public final String getCategory() {
        return category;
    }

    public final int getStartNumber() {
        return startNumber;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public final int getRank() {
        return rank;
    }

    public final String getLocation(){
        return location;
    }

    public String getRankString(Resources res) {
        if (rank == Helper.Disqet.AUSSER_KONKURENZ)
            return res.getString(R.string.aK);
        if (rank == Helper.intnull)
            return "";
        return rank + ".";
    }

    public String getFinishTimeString(Resources res) {
        if (finishTime == Helper.Disqet.POSTEN_FALSCH)
            return res.getString(R.string.postenfalsch);
        if (finishTime == Helper.Disqet.DNS)
            return res.getString(R.string.dns);
        if (finishTime == Helper.Disqet.DISQET)
            return res.getString(R.string.disqet);
        if (finishTime == Helper.Disqet.POSTEN_FEHLT)
            return res.getString(R.string.postenfehlt);
        if (finishTime == Helper.Disqet.AUFGEGEBEN)
            return res.getString(R.string.aufgegeben);
        if (finishTime == Helper.Disqet.NICHT_KLASSIERT)
            return res.getString(R.string.nichtklassiert);
        if (finishTime == Helper.Disqet.UEBERZEIT)
            return res.getString(R.string.ueberzeit);
        return DateUtils.formatElapsedTime(finishTime / 1000);
    }
}

