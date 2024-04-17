package ch.swisso;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarManager {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final MyActivity act;
    private Account account;
    private long[] calIds;
    private static final int IDX_ALL_EVENTS = 0;
    private static final int IDX_FAV_EVENTS = 1;

    private boolean updating = false;

    public CalendarManager(MyActivity activity) {
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
        setupCals();
    }

    private void setupCals() {
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
        calIds = new long[2];
        String[] calNames = new String[calIds.length];
        calNames[IDX_FAV_EVENTS] = act.getString(R.string.cal_fav);
        calNames[IDX_ALL_EVENTS] = act.getString(R.string.cal_all);
        int[] colors = new int[]{Color.LTGRAY, Color.DKGRAY};
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
                calIds[i] = Long.parseLong(result.getLastPathSegment());
            } else {
                cursor.moveToFirst();
                calIds[i] = cursor.getLong(0);
            }
            cursor.close();
        }
    }

    private Uri asSyncAdapter(@NonNull Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, account.name)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, account.type).build();
    }

    public void updateEvents(ArrayList<Event> events) {
        if (!updating) {
            updating = true;
            final Helper.ProcessedListener listener = successful -> handler.post(() -> {
                if (successful) {
                    updating = false;
                }
            });

            Runnable background = () -> {
                asyncUpdateEvents(events);
                listener.onProcessed(true);
            };

            executor.execute(background);
        }
    }

    public void updateFavEvent(Event event){
        updateEvent(event, calIds[IDX_FAV_EVENTS]);
    }

    private void asyncUpdateEvents(ArrayList<Event> events){
        if (calIds != null) {
            ArrayList<String> checkedIds = new ArrayList<>();
            ArrayList<String> checkedFavIds = new ArrayList<>();
            for (Event event : events) {
                updateEvent(event, calIds[IDX_ALL_EVENTS]);
                checkedIds.add("" + event.getId());
                if (event.isFavorit()) {
                    updateEvent(event, calIds[IDX_FAV_EVENTS]);
                    checkedFavIds.add("" + event.getId());
                }
            }
            String[] ids = new String[2];
            ids[IDX_ALL_EVENTS] = String.join(", ", checkedIds);
            ids[IDX_FAV_EVENTS] = String.join(", ", checkedFavIds);
            for(int i = 0; i < calIds.length; i++){
                act.getContentResolver().delete(asSyncAdapter(Events.CONTENT_URI), Events.CALENDAR_ID + " = " + calIds[i] + " AND " + Events._SYNC_ID + " NOT IN (" + ids[i] + ")", null);
            }
        }
    }

    private void updateEvent(@NonNull Event event, long calId) {
        ContentResolver cr = act.getContentResolver();
        String selection = Events._SYNC_ID + " = " + event.getId() + " AND " + Events.CALENDAR_ID + " = " + calId;
        Cursor c = cr.query(asSyncAdapter(Events.CONTENT_URI), new String[]{Events.TITLE, Events.DTSTART, Events.DTEND, Events.ALL_DAY, Events.EVENT_LOCATION, Events.DESCRIPTION}, selection, null, null);
        if (c.getCount() == 0) {
            ContentValues values = createUpdateEventCV(event);
            values.put(Events.CALENDAR_ID, calId);
            values.put(Events._SYNC_ID, event.getId());
            cr.insert(asSyncAdapter(Events.CONTENT_URI), values);
        } else {
            c.moveToFirst();
            boolean needsUpdate = event.calNeedsUpdate(c, act);
            if (needsUpdate) {
                cr.update(asSyncAdapter(Events.CONTENT_URI), createUpdateEventCV(event), selection, null);
            }
        }
        c.close();
    }

    @NonNull
    private ContentValues createUpdateEventCV(@NonNull Event event) {
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, event.getName());
        values.put(Events.DTSTART, event.getBeginDate().getTime());
        values.put(Events.DTEND, (event.getEndDate() == null ? event.getBeginDate() : event.getEndDate()).getTime() + 86400000L);
        values.put(Events.ALL_DAY, 1);
        values.put(Events.EVENT_TIMEZONE, "UTC");
        values.put(Events.DESCRIPTION, act.getString(R.string.open_in_swisso_app) + " " + event.getDeeplinkUrl());
        if (event.getMap() != null) {
            values.put(Events.EVENT_LOCATION, event.getMap());
        }
        return values;
    }
}
