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

    public void onResult(@NonNull MyHttpClient.RequestCodes requestCode, int id, String result, MyFragment fragment) {
        switch (requestCode) {
            case Eventliste:
                loadEvents(result, fragment);
                return;
            case Laeufer:
                loadLaeufer(result, id, fragment);
                return;
            case Messages:
                loadMessages(result);
        }
    }

    public boolean sendEventRequest(OverviewFragment fragment) {
        return sendRequest("https://api.swisso.severinlaasch.ch/events", MyHttpClient.RequestCodes.Eventliste, -1, fragment);
    }

    public boolean sendLaeuferRequest(int id, ListFragment fragment) {
        return sendRequest("https://api.swisso.severinlaasch.ch/laeufer?event_id=" + id, MyHttpClient.RequestCodes.Laeufer, id, fragment);
    }

    public boolean sendMessageRequest() {
        return sendRequest("http://api.swisso.severinlaasch.ch/messages", MyHttpClient.RequestCodes.Messages, -1, null);
    }

    private boolean sendRequest(String url, MyHttpClient.RequestCodes code, int id, MyFragment fragment){
        if(act.isNetworkAvailable()){
            httpClient.sendStringRequest(this, url, code, id, fragment);
            return true;
        }
        else{
            return false;
        }
    }

    private void loadEvents(String json, MyFragment fragment) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                JSONArray array = new JSONArray(json);
                Daten daten = act.getDaten();
                Cursor d = daten.getEvents();
                ArrayList<Integer> ids = new ArrayList<>();
                d.moveToFirst();
                while (!d.isAfterLast()) {
                    ids.add(Helper.getInt(d, SQLiteHelper.COLUMN_ID));
                    d.moveToNext();
                }
                d.close();
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
                fragment.reloadList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadLaeufer(String json, int eventId, MyFragment fragment) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                JSONArray array = new JSONArray(json);
                Daten daten = act.getDaten();
                Cursor c = daten.getLaeuferByEvent(eventId);
                ArrayList<Integer> ids = new ArrayList<>();
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    ids.add(Helper.getInt(c, SQLiteHelper.COLUMN_ID));
                    c.moveToNext();
                }
                c.close();
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
                fragment.reloadList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadMessages(String json) {
        try {
            JSONArray array = new JSONArray(json);
            Daten daten = act.getDaten();
            Cursor c = daten.getMessages();
            ArrayList<Integer> ids = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                ids.add(Helper.getInt(c, SQLiteHelper.COLUMN_ID));
                c.moveToNext();
            }
            c.close();
            for (int black : Helper.blacklistMessages) {
                if (!ids.contains(black)) {
                    ids.add(black);
                } else {
                    daten.deleteMessage(black);
                }
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonMessage = array.getJSONObject(i);
                int id = jsonMessage.getInt(SQLiteHelper.COLUMN_ID);
                if (!ids.contains(id)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SQLiteHelper.COLUMN_ID, id);
                    contentValues.put(SQLiteHelper.COLUMN_VIEWED, 0);
                    contentValues.put(SQLiteHelper.COLUMN_MESSAGE, jsonMessage.getString("content"));
                    daten.insertMessage(contentValues);
                } else {
                    ids.remove((Integer) id);
                }
            }
            for (int id : ids) {
                daten.deleteMessage(id);
            }
            act.showMessages();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
