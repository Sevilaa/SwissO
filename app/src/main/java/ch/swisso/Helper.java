package ch.swisso;

import android.database.Cursor;
import android.net.Uri;

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

    public static final int[] blacklistMessages = new int[]{1};

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

    public static boolean getBool(@NonNull Cursor cursor, String column){
        int index = cursor.getColumnIndex(column);
        return !cursor.isNull(index) && cursor.getInt(index) > 0;
    }

    public interface Keys {
        String sorting_startlist_column = "sorting_startlist_column";
        String sorting_startlist_ascending = "sorting_startlist_ascending";
        String sorting_ranglist_column = "sorting_ranglist_column";
        String sorting_ranglist_ascending = "sorting_ranglist_ascending";
        String intent_event = "event_id";
        String intent_navID = "nav_id";
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

    public interface ZeitStatus{
        int PROV = 2;
        int DEFINITIV = 3;
    }
}
