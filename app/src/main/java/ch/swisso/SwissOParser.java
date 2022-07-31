package ch.swisso;

import android.content.ContentValues;
import android.database.Cursor;

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
                LoadLaeufer(result);
        }
    }

    public void sendEventRequest() {
        httpClient.sendStringRequest(this, "https://api.swisso.severinlaasch.ch/events", MyHttpClient.RequestCodes.Eventliste, -1);
    }

    public void sendLaeuferRequest(int id) {
        httpClient.sendStringRequest(this, "https://api.swisso.severinlaasch.ch/laeufer?event_id=" + id, MyHttpClient.RequestCodes.Laeufer, id);
    }

    public void LoadEvents(String json) {
        try {
            JSONArray array = new JSONArray(json);
            ArrayList<Event> events = new ArrayList<>();
            Daten daten = act.getDaten();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonEvent = array.getJSONObject(i);
                Event e = new Event(jsonEvent);
                events.add(e);
                Cursor c = daten.getEventById(e.getId());
                if (c.getCount() == 0) {
                    daten.insertEvent(e);
                } else {
                    daten.updateEvent(e);
                }
                c.close();
            }
            act.reloadEvents(events);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void LoadLaeufer(String json) {
        try {
            JSONArray array = new JSONArray(json);
            Daten daten = act.getDaten();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonLauefer = array.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                int id = jsonLauefer.getInt(SQLiteHelper.COLUMN_ID);
                contentValues.put(SQLiteHelper.COLUMN_ID, id);
                contentValues.put(SQLiteHelper.COLUMN_NAME, Helper.getString(jsonLauefer, SQLiteHelper.COLUMN_NAME));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_JAHRGANG))
                    contentValues.put(SQLiteHelper.COLUMN_JAHRGANG, jsonLauefer.getInt(SQLiteHelper.COLUMN_JAHRGANG));
                contentValues.put(SQLiteHelper.COLUMN_CLUB, Helper.getString(jsonLauefer, SQLiteHelper.COLUMN_CLUB));
                contentValues.put(SQLiteHelper.COLUMN_KATEGORIE, Helper.getString(jsonLauefer, SQLiteHelper.COLUMN_KATEGORIE));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_STARTNUMMER))
                    contentValues.put(SQLiteHelper.COLUMN_STARTNUMMER, jsonLauefer.getInt(SQLiteHelper.COLUMN_STARTNUMMER));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_STARTZEIT))
                    contentValues.put(SQLiteHelper.COLUMN_STARTZEIT, jsonLauefer.getInt(SQLiteHelper.COLUMN_STARTZEIT));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_ZIELZEIT))
                    contentValues.put(SQLiteHelper.COLUMN_ZIELZEIT, jsonLauefer.getInt(SQLiteHelper.COLUMN_ZIELZEIT));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_RANG))
                    contentValues.put(SQLiteHelper.COLUMN_RANG, jsonLauefer.getInt(SQLiteHelper.COLUMN_RANG));
                contentValues.put(SQLiteHelper.COLUMN_EVENT, jsonLauefer.getInt(SQLiteHelper.COLUMN_EVENT));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_STARTLISTE))
                    contentValues.put(SQLiteHelper.COLUMN_STARTLISTE, jsonLauefer.getInt(SQLiteHelper.COLUMN_STARTLISTE));
                if (!jsonLauefer.isNull(SQLiteHelper.COLUMN_RANGLISTE))
                    contentValues.put(SQLiteHelper.COLUMN_RANGLISTE, jsonLauefer.getInt(SQLiteHelper.COLUMN_RANGLISTE));
                Cursor c = daten.getLaeuferById(id);
                if (c.getCount() == 0) {
                    daten.insertLaeufer(contentValues);
                } else {
                    daten.updateLaeufer(contentValues, id);
                }
                c.close();
            }
            act.reloadList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
