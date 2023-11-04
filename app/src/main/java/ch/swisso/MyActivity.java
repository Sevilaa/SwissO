package ch.swisso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class MyActivity extends AppCompatActivity {

    protected Daten daten;
    protected SwissOParser parser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daten = new Daten(this);
        parser = new SwissOParser(this);
    }

    public Daten getDaten() {
        return daten;
    }

    public SwissOParser getParser() {
        return parser;
    }

    @Override
    protected void onDestroy() {
        daten.Close();
        super.onDestroy();
    }

    public void openWebBrowser(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public void insertToCalendar(@NonNull Event e) {
        Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT);
        insertCalendarIntent.setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, e.getName()) // Simple title
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, e.getBeginDate().getTime()) // Only date part is considered when ALL_DAY is true
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, e.getEndDate() != null ? e.getEndDate().getTime() : e.getBeginDate().getTime())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, e.getCalenderLocation(Event.Maps.Google));
        Uri ausschreibung = e.getUri(Event.UriArt.Ausschreibung);
        if (ausschreibung != null) {
            insertCalendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, ausschreibung.toString());
        }
        startActivity(insertCalendarIntent);
    }

    public abstract void openEventDetails(@NonNull Event e, @NonNull Event.UriArt uriArt);
}
