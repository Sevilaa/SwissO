package ch.swisso;

import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class Helper {

    public static final int intnull = 23904857;

    public static final int kind_fuss = 1;
    public static final int kind_ski = 2;
    public static final int kind_bike = 3;
    public static final String pref_file = "default_pref";

    @NonNull
    public static Calendar getToday() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String getZielzeit(int time, Resources res) {
        if (time == Disqet.POSTEN_FALSCH)
            return res.getString(R.string.postenfalsch);
        if (time == Disqet.DNS)
            return res.getString(R.string.dns);
        if (time == Disqet.DISQET)
            return res.getString(R.string.disqet);
        if (time == Disqet.POSTEN_FEHLT)
            return res.getString(R.string.postenfehlt);
        if (time == Disqet.AUFGEGEBEN)
            return res.getString(R.string.aufgegeben);
        if (time == Disqet.NICHT_KLASSIERT)
            return res.getString(R.string.nichtklassiert);
        if (time == Disqet.UEBERZEIT)
            return res.getString(R.string.ueberzeit);
        return DateUtils.formatElapsedTime(time / 1000);
    }

    public static String getRang(int rang, Resources res) {
        if (rang == Disqet.AUSSER_KONKURENZ)
            return res.getString(R.string.aK);
        if (rang == intnull)
            return "";
        return rang + ".";
    }

    public static Uri newUri(String s) {
        if (s == null) {
            return null;
        }
        if (s.trim().equals("")) {
            return null;
        }
        return Uri.parse(s);
    }

    public static float getFloat(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (cursor.isNull(index)) {
            return Helper.intnull;
        }
        return cursor.getFloat(index);
    }

    public static int getInt(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getInt(index);
    }

    @Nullable
    public static String getString(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    public static long getLong(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getLong(index);
    }

    public static double getDouble(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getDouble(index);
    }

    public static boolean isNull(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index);
    }

    @Nullable
    public static Uri getUri(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : newUri(cursor.getString(index));
    }

    @Nullable
    public static Date getDate(@NonNull Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }

    public interface Keys {
        String sorting_startlist_column = "sorting_startlist_column";
        String sorting_startlist_ascending = "sorting_startlist_ascending";
        String sorting_ranglist_column = "sorting_ranglist_column";
        String sorting_ranglist_ascending = "sorting_ranglist_ascending";
    }

    public interface Defaults {
        String sorting_startlist_column = SQLiteHelper.COLUMN_STARTNUMMER;
        boolean sorting_startlist_ascending = true;
        String sorting_ranglist_column = SQLiteHelper.COLUMN_KATEGORIE;
        boolean sorting_ranglist_ascending = true;
    }

    public interface Disqet {
        int POSTEN_FALSCH = -2;
        int POSTEN_FEHLT = -3;
        int AUFGEGEBEN = -4;
        int DISQET = -5;
        int DNS = -6;
        int UEBERZEIT = -7;
        int NICHT_KLASSIERT = -8;
        int AUSSER_KONKURENZ = 10000;
    }
}
