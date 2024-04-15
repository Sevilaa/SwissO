package ch.swisso;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class CalendarManager {

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.CALENDAR_ACCESS_LEVEL                  // 3
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    private final MainActivity act;
    private Account account;


    public CalendarManager(MainActivity activity) {
        act = activity;
        checkPermissions();
    }

    private void checkPermissions() {
        if (!(ContextCompat.checkSelfPermission(act, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
            ActivityResultLauncher<String> resultLauncher = act.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkPermissions();
                } else {
                    Toast.makeText(act, "Events werden nicht im Kalender angezeigt", Toast.LENGTH_SHORT).show(); //TODO
                }
            });
            resultLauncher.launch(Manifest.permission.READ_CALENDAR);
            return;
        }
        if (!(ContextCompat.checkSelfPermission(act, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
            ActivityResultLauncher<String> resultLauncher = act.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkPermissions();
                } else {
                    Toast.makeText(act, "Events werden nicht im Kalender angezeigt", Toast.LENGTH_SHORT).show(); //TODO
                }
            });
            resultLauncher.launch(Manifest.permission.WRITE_CALENDAR);
            return;
        }
        init();
    }

    private void init() {
        AccountManager accountManager = AccountManager.get(act);
        Account[] accounts = accountManager.getAccountsByType(act.getString(R.string.account_type));
        if (accounts.length == 0) {
            account = new Account(act.getString(R.string.account_name), act.getString(R.string.account_type));
            boolean success = accountManager.addAccountExplicitly(account, "password", null);
            if (!success) {
                Log.e("SwissO", "Account creation failed");
                return;
            }
        } else {
            account = accounts[0];
        }
        String[] calNames = new String[]{act.getString(R.string.cal_fav), act.getString(R.string.cal_all)};
        int[] colors = new int[]{Color.LTGRAY, Color.DKGRAY};
        long[] calIDs = new long[2];
        for (int i = 0; i < calNames.length; i++) {
            ContentResolver cr = act.getContentResolver();
            Uri calQuery = CalendarContract.Calendars.CONTENT_URI;
            Cursor cursor = cr.query(calQuery, new String[]{Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.ACCOUNT_TYPE},
                    Calendars.ACCOUNT_TYPE + " = '" + account.type + "' AND " + Calendars.CALENDAR_DISPLAY_NAME + " = '" + calNames[i] + "'", null, null);
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(Calendars.ACCOUNT_NAME, account.name);
                values.put(Calendars.ACCOUNT_TYPE, account.type);
                values.put(Calendars.NAME, calNames[i]);
                values.put(Calendars.CALENDAR_DISPLAY_NAME, calNames[i]);
                values.put(Calendars.CALENDAR_COLOR, colors[i]);
                values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_READ);
                values.put(Calendars.OWNER_ACCOUNT, account.name);
                values.put(Calendars.VISIBLE, 1);
                values.put(Calendars.SYNC_EVENTS, 1);
                Uri result = cr.insert(asSyncAdapter(Calendars.CONTENT_URI), values);
                calIDs[i] = Long.parseLong(result.getLastPathSegment());
            } else {
                cursor.moveToFirst();
                calIDs[i] = cursor.getLong(0);
            }
            cursor.close();
        }

        Log.e("SwissO", "Ids: " + calIDs[0] + " " + calIDs[1]);

        queryCals();
    }


    private void queryCals() {
        ContentResolver cr = act.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cursor = cr.query(uri, EVENT_PROJECTION, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;
            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.e("SwissO", "" + calID + " " + displayName + "  " + accountName + " " + ownerName);
        }
        cursor.close();

        ContentValues v = new ContentValues();
        v.put(CalendarContract.Calendars.VISIBLE, 1);
        v.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        Uri update = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, 15);
        cr.update(update, v, null, null);


        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2024, 4, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2024, 4, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, 1713225600000L);
        values.put(Events.DTEND, 1713312000000L);
        values.put(Events.TITLE, "Jazzercise");
        values.put(Events.DESCRIPTION, "Group workout");
        values.put(Events.CALENDAR_ID, 15);
        values.put(Events.EVENT_TIMEZONE, "UTC");
        Uri result = cr.insert(asSyncAdapter(Events.CONTENT_URI), values);

        Cursor c = cr.query(asSyncAdapter(Events.CONTENT_URI), new String[]{Events._ID, Events.TITLE}, Events.CALENDAR_ID + " = 15", null, null);
        Log.e("SwissO", "Count: " + c.getCount());
        if (c.getCount() > 0) {
            c.moveToFirst();
            Log.e("SwissO", "" + c.getLong(0) + c.getString(1));
        }
        c.close();
    }

    private Uri asSyncAdapter(@NonNull Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account.name)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, account.type).build();
    }
}
