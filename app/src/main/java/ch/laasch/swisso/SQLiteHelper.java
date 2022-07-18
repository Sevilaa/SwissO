package ch.laasch.swisso;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SwissO.dat";
    public static final int DATABASE_VERSION = 1;

    //Tables
    public static final String TABLE_Profil = "Profil";
    public static final String TABLE_Freunde = "Freunde";
    public static final String TABLE_Laeufer = "Laeufer";
    public static final String TABLE_Clubs = "Clubs";
    public static final String TABLE_Events = "Events";

    //Primary Key
    public static final String COLUMN_ID = "id";

    //Fremdschlüssel
    public static final String COLUMN_Profil = "profil";
    public static final String COLUMN_Event = "event";

    //Table Profil
    public static final String COLUMN_Vorname = "first_name";
    public static final String COLUMN_Nachname = "surname";
    public static final String COLUMN_SI = "sicard";
    public static final String COLUMN_Category = "category";

    //Table Events
    /*public static final String COLUMN_Title = "title";
    public static final String COLUMN_SOLVId = "solvid";
    public static final String COLUMN_Date = "date";
    public static final String COLUMN_Deadline = "deadline";
    public static final String COLUMN_Region = "region";
    public static final String COLUMN_IntKoordN = "skoordn";
    public static final String COLUMN_IntKoordE = "skoorde";
    public static final String COLUMN_Club = "club";
    public static final String COLUMN_Map = "map";
    public static final String COLUMN_LAusschreibung = "ausschreibung";
    public static final String COLUMN_LWeisungen = "weisungen";
    public static final String COLUMN_LRangliste = "rangliste";
    public static final String COLUMN_LLiveRangliste = "liverangliste";
    public static final String COLUMN_LStartliste = "startliste";
    public static final String COLUMN_LAnmeldung = "anmeldung";
    public static final String COLUMN_LMutation = "mutation";
    public static final String COLUMN_EntryPortal = "entryportal";*/

    //Table Laeufer
    public static final String COLUMN_Jahrgang = "jahrgang";
    public static final String COLUMN_Startnummer = "startnummer";
    public static final String COLUMN_Startzeit = "startzeit";
    public static final String COLUMN_Zielzeit = "zielzeit";
    public static final String COLUMN_Rang = "rang";

    //Table Events
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BEGIN_DATE = "begin_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_KIND = "kind";
    public static final String COLUMN_NIGHT = "night";
    public static final String COLUMN_NATIONAL = "national";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_CLUB = "club";
    public static final String COLUMN_MAP = "map";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_INT_NORD = "int_nord";
    public static final String COLUMN_INT_EAST = "int_east";
    public static final String COLUMN_ENTRYPORTAL = "entryportal";
    public static final String COLUMN_AUSSCHREIBUNG = "ausschreibung";
    public static final String COLUMN_LIVE_RESULTATE = "live_resultate";
    public static final String COLUMN_WEISUNGEN = "weisungen";
    public static final String COLUMN_ANMELDUNG = "anmeldung";
    public static final String COLUMN_RANGLISTE = "rangliste";
    public static final String COLUMN_STARTLISTE = "startliste";
    public static final String COLUMN_TEILNEHMERLISTE = "teilnehmerliste";
    public static final String COLUMN_MUTATION = "mutation";

    private static final String SQL_Profil = "CREATE TABLE IF NOT EXISTS " + TABLE_Profil + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_Vorname + " VARCHAR(15)," +
            COLUMN_Nachname + " VARCHAR(15)," +
            COLUMN_SI + " INTEGER," +
            COLUMN_Category + " VARCHAR(15))";

    private static final String SQL_Laeufer = "CREATE TABLE IF NOT EXISTS " + TABLE_Laeufer + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(31) NOT NULL," +
            COLUMN_Jahrgang + " INTEGER," +
            COLUMN_CLUB + " VARCHAR(31)," +
            COLUMN_Category + " VARCHAR(15) NOT NULL," +
            COLUMN_Startnummer + " INTEGER," +
            COLUMN_Startzeit + " INTEGER," +
            COLUMN_Zielzeit + " INTEGER," +
            COLUMN_Rang + " INTEGER," +
            COLUMN_Event + " INTEGER NOT NULL)";

    private static final String SQL_Freunde = "CREATE TABLE IF NOT EXISTS " + TABLE_Freunde + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(31) NOT NULL," +
            COLUMN_Profil + " INTEGER NOT NULL)";

    private static final String SQL_Clubs = "CREATE TABLE IF NOT EXISTS " + TABLE_Clubs + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(31) NOT NULL," +
            COLUMN_Profil + " INTEGER NOT NULL)";

    private static final String SQL_Events = "CREATE TABLE IF NOT EXISTS " + TABLE_Events + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME + " TEXT," +
            COLUMN_BEGIN_DATE + " INTEGER," +
            COLUMN_END_DATE + " INTEGER," +
            COLUMN_DEADLINE + " INTEGER," +
            COLUMN_KIND + " INTEGER," +
            COLUMN_NIGHT + " INTEGER," +
            COLUMN_NATIONAL + " INTEGER," +
            COLUMN_REGION + " TEXT," +
            COLUMN_CLUB + " TEXT," +
            COLUMN_MAP + " TEXT," +
            COLUMN_LOCATION + " TEXT," +
            COLUMN_INT_NORD + " REAL," +
            COLUMN_INT_EAST + " REAL," +
            COLUMN_ENTRYPORTAL + " INTEGER," +
            COLUMN_AUSSCHREIBUNG + " TEXT," +
            COLUMN_LIVE_RESULTATE + " TEXT," +
            COLUMN_WEISUNGEN + " TEXT," +
            COLUMN_ANMELDUNG + " TEXT," +
            COLUMN_RANGLISTE + " TEXT," +
            COLUMN_STARTLISTE + " TEXT," +
            COLUMN_TEILNEHMERLISTE + " TEXT," +
            COLUMN_MUTATION + " TEXT)";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteHelper.SQL_Profil);
        db.execSQL(SQLiteHelper.SQL_Freunde);
        db.execSQL(SQLiteHelper.SQL_Clubs);
        db.execSQL(SQLiteHelper.SQL_Events);
        db.execSQL(SQLiteHelper.SQL_Laeufer);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion < 3) {
//            db.ExecSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_Profil);
//            db.ExecSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_Laeufer);
//        }
        onCreate(db);
    }
}
