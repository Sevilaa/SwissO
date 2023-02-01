package ch.swisso;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Event> events = new ArrayList<>();
    private Daten daten;
    private SwissOParser parser;
    private Event selectedEvent;
    private BottomNavigationView navigation;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActViewModel viewModel = new ViewModelProvider(this).get(ActViewModel.class);
        daten = new Daten(this);
        parser = new SwissOParser(this);

        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        initEvents();

        navigation = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        viewModel.setRefreshingEvents(true);

        parser.sendMessageRequest();
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    public final void initEvents() {
        Cursor cursor = daten.getEvents();
        events.clear();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event e = new Event(cursor);
            events.add(e);
            cursor.moveToNext();
        }
        cursor.close();

        int index = events.indexOf(selectedEvent);
        if (index != -1) {
            selectedEvent = events.get(index);
        } else { // Get the next Event that happens
            for (int i = 0; i < events.size(); i++) {
                if (selectedEvent == null && events.get(i).getBeginDate().getTime() >= Helper.getToday().getTimeInMillis()) {
                    selectedEvent = events.get(i);
                    i = events.size();
                }
            }
        }
        if (selectedEvent == null && events.size() > 0) {
            selectedEvent = events.get(events.size() - 1);
        }
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public ArrayList<Event> getEvents() {
        return events;
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

    public final void openEventDetails(Event e, @NonNull Event.UriArt uriArt) {
        selectedEvent = e;
        switch (uriArt) {
            case Rangliste:
                navigation.setSelectedItemId(R.id.ranglistFragment);
                break;
            case Startliste:
                navigation.setSelectedItemId(R.id.startlistFragment);
                break;
            case Kalender:
                insertToCalendar(e);
                break;
            default:
                openWebBrowser(e.getUri(uriArt));
                break;
        }
    }

    public void insertToCalendar(@NonNull Event e) {
        Intent insertCalendarIntent = new Intent(Intent.ACTION_INSERT);
        insertCalendarIntent.setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, e.getName()) // Simple title
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, e.getBeginDate().getTime()) // Only date part is considered when ALL_DAY is true; Same as DTSTART
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, e.getEndDate() != null ? e.getEndDate().getTime() : e.getBeginDate().getTime()) // Only date part is considered when ALL_DAY is true
                .putExtra(CalendarContract.Events.EVENT_LOCATION, e.getMap());
        Uri ausschreibung = e.getUri(Event.UriArt.Ausschreibung);
        if(ausschreibung != null) {
            insertCalendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, e.getUri(Event.UriArt.Ausschreibung).toString());
        }
        startActivity(insertCalendarIntent);
    }

    public void openWebBrowser(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        boolean available = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        findViewById(R.id.tv_internet_connection).setVisibility(available ? View.GONE : View.VISIBLE);
        return available;
    }

    public void showMessages() {
        Cursor c = getDaten().getUnreadMessages();
        if (c.getCount() > 0) {
            String title = getResources().getString(R.string.newmessages);
            ArrayList<String> messages = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                messages.add(Helper.getString(c, SQLiteHelper.COLUMN_MESSAGE));
                c.moveToNext();
            }
            c.close();
            String content = String.join("\n\n", messages);

            MessageDialog dialog = new MessageDialog();
            dialog.init(daten, content, title);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    public static class ActViewModel extends ViewModel {
        private final MutableLiveData<Boolean> refreshingEvents = new MutableLiveData<>();

        public void setRefreshingEvents(boolean b) {
            refreshingEvents.setValue(b);
        }

        public MutableLiveData<Boolean> getRefreshingEvents() {
            return refreshingEvents;
        }
    }
}