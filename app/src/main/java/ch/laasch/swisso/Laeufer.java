package ch.laasch.swisso;

import android.database.Cursor;

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
        jahrgang = Helper.getInt(cursor, SQLiteHelper.COLUMN_Jahrgang);
        club = Helper.getString(cursor, SQLiteHelper.COLUMN_CLUB);
        category = Helper.getString(cursor, SQLiteHelper.COLUMN_Category);
        startnummer = Helper.getInt(cursor, SQLiteHelper.COLUMN_Startnummer);
//        startzeit = Helper.getTime(cursor, SQLiteHelper.COLUMN_Startzeit);
//        zielzeit = Helper.getTime(cursor, SQLiteHelper.COLUMN_Zielzeit);
        rang = Helper.getInt(cursor, SQLiteHelper.COLUMN_Rang);
    }

    private int id;
    private String name;
    private int jahrgang;
    private String club;
    private String category;
    private int startnummer;
    private int rang;
//    private TimeSpan startzeit;
//    private TimeSpan zielzeit;

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

//    public final TimeSpan getStartzeit() {
//        return startzeit;
//    }
//
//    public final TimeSpan getZielzeit() {
//        return zielzeit;
//    }

    public final int getRang() {
        return rang;
    }
}

