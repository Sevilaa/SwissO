package ch.swisso;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SwissO.db";
    public static final int DATABASE_VERSION = 2;

    //Tables
    public static final String TABLE_Freunde = "Freunde";
    public static final String TABLE_Laeufer = "Laeufer";
    public static final String TABLE_Clubs = "Clubs";
    public static final String TABLE_Events = "Events";
    public static final String TABLE_Messages = "Messages";

    //Primary Key
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AUTO_ID = "_id";

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

    //Table Laeufer
    public static final String COLUMN_JAHRGANG = "jahrgang";
    public static final String COLUMN_KATEGORIE = "kategorie";
    public static final String COLUMN_STARTNUMMER = "startnummer";
    public static final String COLUMN_STARTZEIT = "startzeit";
    public static final String COLUMN_ZIELZEIT = "zielzeit";
    public static final String COLUMN_RANG = "rang";
    public static final String COLUMN_EVENT = "event";

    //Table Messages
    public static final String COLUMN_VIEWED = "viewed";
    public static final String COLUMN_MESSAGE = "message";

    private static final String SQL_Freunde = "CREATE TABLE IF NOT EXISTS " + TABLE_Freunde + "(" +
            COLUMN_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(31) NOT NULL)";

    private static final String SQL_Clubs = "CREATE TABLE IF NOT EXISTS " + TABLE_Clubs + "(" +
            COLUMN_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(31) NOT NULL)";

    private static final String SQL_Messages = "CREATE TABLE IF NOT EXISTS " + TABLE_Messages + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY," +
            COLUMN_VIEWED + " INTEGER NOT NULL," +
            COLUMN_MESSAGE + " TEXT NOT NULL)";

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

    private static final String SQL_Laeufer = "CREATE TABLE IF NOT EXISTS " + TABLE_Laeufer + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME + " VARCHAR(31) NOT NULL,"
            + COLUMN_JAHRGANG + " INTEGER,"
            + COLUMN_CLUB + " VARCHAR(31),"
            + COLUMN_KATEGORIE + " VARCHAR(15) NOT NULL,"
            + COLUMN_STARTNUMMER + " INTEGER,"
            + COLUMN_STARTZEIT + " INTEGER,"
            + COLUMN_ZIELZEIT + " INTEGER,"
            + COLUMN_RANG + " INTEGER,"
            + COLUMN_EVENT + " INTEGER NOT NULL,"
            + COLUMN_STARTLISTE + " INTEGER,"
            + COLUMN_RANGLISTE + " INTEGER)";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(SQL_Freunde);
        db.execSQL(SQL_Clubs);
        db.execSQL(SQL_Events);
        db.execSQL(SQL_Laeufer);
        db.execSQL(SQL_Messages);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Laeufer);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Events);
        }
        onCreate(db);
    }
}
