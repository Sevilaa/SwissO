package ch.swisso;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CalendarContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Log.w("SwissO", uri.toString());
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.w("SwissO", uri.toString());
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.w("SwissO", uri.toString());
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        Log.w("SwissO", uri.toString());
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        Log.w("SwissO", uri.toString());
        return 0;
    }
}
