package ch.swisso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwissOParser {

    private final MyActivity act;
    private final MyHttpClient httpClient;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private interface ProcessedListener {
        void onProcessed(boolean successful);
    }

    public SwissOParser(MyActivity act) {
        this.act = act;
        httpClient = new MyHttpClient(act);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return networkCapabilities != null;
        } else {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isAvailable();
        }
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

    public boolean sendEventRequest(EventListFragment fragment) {
        return sendRequest("https://api.swiss-o.ch/events", MyHttpClient.RequestCodes.Eventliste, -1, fragment);
    }

    public boolean sendLaeuferRequest(int id, ListFragment fragment) {
        return sendRequest("https://api.swiss-o.ch/laeufer?event_id=" + id, MyHttpClient.RequestCodes.Laeufer, id, fragment);
    }

    public boolean sendMessageRequest() {
        return sendRequest("https://api.swiss-o.ch/messages", MyHttpClient.RequestCodes.Messages, -1, null);
    }

    private boolean sendRequest(String url, MyHttpClient.RequestCodes code, int id, MyFragment fragment) {
        if (isNetworkAvailable()) {
            httpClient.sendStringRequest(this, url, code, id, fragment);
            return true;
        } else {
            return false;
        }
    }

    private void loadEvents(String json, MyFragment fragment) {
        final ProcessedListener listener = successful -> handler.post(() -> {
            if (successful) {
                fragment.reloadList();
            }
        });

        Runnable background = () -> {
            boolean result = act.getDaten().updateEventsFromJson(json);
            listener.onProcessed(result);
        };

        executor.execute(background);
    }

    private void loadLaeufer(String json, int eventId, MyFragment fragment) {
        final ProcessedListener listener = successful -> handler.post(() -> {
            if (successful) {
                fragment.reloadList();
            }
        });

        Runnable background = () -> {
            boolean result = act.getDaten().updateLaeuferFromJson(json, eventId);
            listener.onProcessed(result);
        };

        executor.execute(background);
    }

    private void loadMessages(String json) {
        if (act instanceof MainActivity) {
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
                ((MainActivity) act).showMessages();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
