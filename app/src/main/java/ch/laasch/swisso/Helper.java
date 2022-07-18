package ch.laasch.swisso;

import android.database.Cursor;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public abstract class Helper {

    public static final int intnull = 23904857;

    public static final int kind_fuss = 1;
    public static final int kind_ski = 2;
    public static final int kind_bike = 3;

    public static Calendar getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
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

    public static float getFloat(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        if (cursor.isNull(index)) {
            return Helper.intnull;
        }
        return cursor.getFloat(index);
    }

    public static int getInt(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getInt(index);
    }

    public static String getString(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    public static long getLong(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getLong(index);
    }

    public static double getDouble(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? Helper.intnull : cursor.getDouble(index);
    }

    public static boolean isNull(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index);
    }

    public static Uri getUri(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : newUri(cursor.getString(index));
    }

    public static Date getDate(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return cursor.isNull(index) ? null : new Date(cursor.getLong(index));
    }

    public static String getString(JSONObject json, String field) throws JSONException {
        return !json.isNull(field) ? json.getString(field) : null;
    }

    public static Uri getUri(JSONObject json, String field) throws JSONException {
        return newUri(getString(json, field));
    }

    public static Date getDate(JSONObject json, String field) throws JSONException {
        return !json.isNull(field) ? new Date(json.getLong(field)) : null;
    }
}
