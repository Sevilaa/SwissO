package ch.laasch.swisso;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SwissOParser extends Parser {

    private MainActivity act;

    public SwissOParser(MainActivity act){
        this.act = act;
    }

    @Override
    public void onResult(MyHttpClient.RequestCodes requestCode, int id, String result) {
        switch (requestCode) {
            case Eventliste:
                LoadEvents(result);
        }
    }

    public void sendEventRequest(){
        act.getHttpClient().sendStringRequest(this, "https://api.swisso.severinlaasch.ch/events", MyHttpClient.RequestCodes.Eventliste, 0);
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
                if(c.getCount() == 0){
                    daten.insertEvent(e);
                }
                else{
                    daten.updateEvent(e);
                }
                c.close();
            }
            act.reloadEvents(events);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
