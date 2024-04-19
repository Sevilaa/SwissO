package ch.swisso;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ch.swisso.SearchManager.EventSearchManager;

public class MainActivity extends MyActivity {

    private final ArrayList<Event> events = new ArrayList<>();
    private Event selectedEvent;
    private EventSearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        searchManager = new EventSearchManager(this, viewModel);
        initEvents();

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_main);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        viewModel.setRefreshingEvents(true);

        parser.sendMessageRequest();
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
        if (selectedEvent == null && !events.isEmpty()) {
            selectedEvent = events.get(events.size() - 1);
        }
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public final void openEventDetails(@NonNull Event e, @NonNull Event.UriArt uriArt) {
        selectedEvent = e;
        switch (uriArt) {
            case Details:
                startEventActivity(e, R.id.detailsFragment);
                break;
            case Liveresultate:
            case Rangliste:
                startEventActivity(e, R.id.ranglistFragment);
                break;
            case Teilnehmerliste:
            case Startliste:
                startEventActivity(e, R.id.startlistFragment);
                break;
            case Kalender:
                insertToCalendar(e);
                break;
            default:
                openWebBrowser(e.getUri(uriArt));
                break;
        }
    }

    public void openFragment(@IdRes int id) {
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setSelectedItemId(id);
    }

    private void startEventActivity(@NonNull Event e, int navigationId) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(Helper.Keys.intent_event, e.getId());
        intent.putExtra(Helper.Keys.intent_navID, navigationId);
        startActivity(intent);
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

    public EventSearchManager getSearchManager() {
        return searchManager;
    }

    public static class MainViewModel extends MyViewModel {
        private final MutableLiveData<Boolean> refreshingEvents = new MutableLiveData<>();

        public void setRefreshingEvents(boolean b) {
            refreshingEvents.setValue(b);
        }

        public MutableLiveData<Boolean> getRefreshingEvents() {
            return refreshingEvents;
        }
    }
}