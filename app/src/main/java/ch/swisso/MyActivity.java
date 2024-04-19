package ch.swisso;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class MyActivity extends AppCompatActivity {

    protected Daten daten;
    protected SwissOParser parser;
    protected CalendarManager calManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daten = new Daten(this);
        parser = new SwissOParser(this);
        calManager = new CalendarManager(this);
    }

    public Daten getDaten() {
        return daten;
    }

    public SwissOParser getParser() {
        return parser;
    }

    public CalendarManager getCalendarManager() {
        return calManager;
    }

    @Override
    protected void onDestroy() {
        daten.close();
        super.onDestroy();
    }

    public void openWebBrowser(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void toggleFav(@NonNull Event event) {
        event.toggleFavorit();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_FAVORIT, event.isFavorit() ? 1 : 0);
        daten.updateEvent(values, event.getId());
        calManager.updateFavEvent(event);
    }

    public void insertToCalendar(@NonNull Event e) {
        Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT);
        insertCalendarIntent.setData(Events.CONTENT_URI)
                .putExtra(Events.TITLE, e.getName()) // Simple title
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, e.getBeginDate().getTime()) // Only date part is considered when ALL_DAY is true
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, e.getEndDate() != null ? e.getEndDate().getTime() : e.getBeginDate().getTime())
                .putExtra(Events.DESCRIPTION, getString(R.string.open_in_swisso_app) + " " + e.getDeeplinkUrl());
        if (e.getMap() != null) {
            insertCalendarIntent.putExtra(Events.EVENT_LOCATION, e.getMap());
        }
        startActivity(insertCalendarIntent);
    }

    public abstract void openEventDetails(@NonNull Event e, @NonNull Event.UriArt uriArt);

    public abstract static class MyViewModel extends ViewModel {
        private final MutableLiveData<Pair<String, String>> searchParams = new MutableLiveData<>();

        public void setSearchParams(Pair<String, String> s) {
            searchParams.setValue(s);
        }

        public boolean isSearchTextEmpty() {
            Pair<String, String> v = searchParams.getValue();
            return v == null || v.first == null || v.first.isEmpty();
        }

        public MutableLiveData<Pair<String, String>> getSearchParams() {
            return searchParams;
        }
    }
}
