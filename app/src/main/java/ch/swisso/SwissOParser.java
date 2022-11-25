package ch.swisso;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SwissOParser {

    private final MainActivity act;
    private final MyHttpClient httpClient;

    public SwissOParser(MainActivity act) {
        this.act = act;
        httpClient = new MyHttpClient(act);
    }

    public void onResult(@NonNull MyHttpClient.RequestCodes requestCode, int id, String result) {
        switch (requestCode) {
            case Eventliste:
                LoadEvents(result);
                return;
            case Laeufer:
                LoadLaeufer(result, id);
        }
    }

    public void sendEventRequest() {
        httpClient.sendStringRequest(this, "https://api.swisso.severinlaasch.ch/events", MyHttpClient.RequestCodes.Eventliste, -1);
    }

    public void sendLaeuferRequest(int id) {
        httpClient.sendStringRequest(this, "https://api.swisso.severinlaasch.ch/laeufer?event_id=" + id, MyHttpClient.RequestCodes.Laeufer, id);
    }

    public void LoadEvents(String json) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                JSONArray array = new JSONArray(json);
                Daten daten = act.getDaten();
                Cursor d = daten.getEvents();
                ArrayList<Integer> ids = new ArrayList<>();
                d.moveToFirst();
                while (!d.isAfterLast()) {
                    ids.add(Helper.getInt(d, SQLiteHelper.COLUMN_ID));
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonEvent = array.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    int id = jsonEvent.getInt(SQLiteHelper.COLUMN_ID);
                    contentValues.put(SQLiteHelper.COLUMN_ID, id);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_NAME);
                    json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_BEGIN_DATE);
                    json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_END_DATE);
                    json2cvLong(jsonEvent, contentValues, SQLiteHelper.COLUMN_DEADLINE);
                    json2cvInt(jsonEvent, contentValues, SQLiteHelper.COLUMN_KIND);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_REGION);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_CLUB);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_MAP);
                    json2cvDouble(jsonEvent, contentValues, SQLiteHelper.COLUMN_INT_NORD);
                    json2cvDouble(jsonEvent, contentValues, SQLiteHelper.COLUMN_INT_EAST);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_AUSSCHREIBUNG);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_WEISUNGEN);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_LIVE_RESULTATE);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_STARTLISTE);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_ANMELDUNG);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_MUTATION);
                    json2cvString(jsonEvent, contentValues, SQLiteHelper.COLUMN_TEILNEHMERLISTE);
                    if (ids.contains(id)) {
                        daten.updateEvent(contentValues, id);
                        ids.remove((Integer) id);
                    } else {
                        daten.insertEvent(contentValues);
                    }
                }
                for (int id : ids) {
                    daten.deleteEvent(id);
                }
                act.initEvents();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void LoadLaeufer(String json, int eventId) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                JSONArray array = new JSONArray(json);
                Daten daten = act.getDaten();
                Cursor c = daten.getLaeuferByEvent(eventId);
                ArrayList<Integer> ids = new ArrayList<>();
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    ids.add(Helper.getInt(c, SQLiteHelper.COLUMN_ID));
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonLauefer = array.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    int id = jsonLauefer.getInt(SQLiteHelper.COLUMN_ID);
                    contentValues.put(SQLiteHelper.COLUMN_ID, id);
                    json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_NAME);
                    json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_CLUB);
                    json2cvString(jsonLauefer, contentValues, SQLiteHelper.COLUMN_KATEGORIE);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_JAHRGANG);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTNUMMER);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTZEIT);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_ZIELZEIT);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANG);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_EVENT);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_STARTLISTE);
                    json2cvInt(jsonLauefer, contentValues, SQLiteHelper.COLUMN_RANGLISTE);
                    if (ids.contains(id)) {
                        daten.updateLaeufer(contentValues, id);
                        ids.remove((Integer) id);
                    } else {
                        daten.insertLaeufer(contentValues);
                    }
                }
                for (int id : ids) {
                    daten.deleteLaeuferById(id);
                }
                act.reloadList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private static void json2cvInt(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getInt(field));
        }
    }

    private static void json2cvLong(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getLong(field));
        }
    }

    private static void json2cvDouble(@NonNull JSONObject json, ContentValues values, String field) throws JSONException {
        if (json.isNull(field)) {
            values.put(field, (String) null);
        } else {
            values.put(field, json.getDouble(field));
        }
    }

    private static void json2cvString(@NonNull JSONObject json, @NonNull ContentValues values, String field) throws JSONException {
        if (!json.isNull(field)) {
            String s = json.getString(field).trim();
            if (!s.equals("")) {
                values.put(field, s);
                return;
            }
        }
        values.put(field, (String) null);
    }

}
