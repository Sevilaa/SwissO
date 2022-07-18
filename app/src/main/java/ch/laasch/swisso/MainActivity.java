package ch.laasch.swisso;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyHttpClient httpClient;
    private MyFragment fragment;
    private Daten daten;
    private Event selectedEvent;
    private ArrayList<Event> events = new ArrayList<>();
    private TabSelection selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daten = new Daten(this);
        httpClient = new MyHttpClient(this);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(item -> {
            setFragment(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(R.id.host_fragment_activity_main, fragment).commit();
            return true;
        });
        setFragment(R.id.navigation_overview);
        getSupportFragmentManager().beginTransaction().replace(R.id.host_fragment_activity_main, fragment).commit();
        initEvents();
    }

    private void setFragment(int itemId) {
        if (itemId == R.id.navigation_overview) {
            fragment = new OverviewFragment();
            selectedTab = TabSelection.Overview;
        } else if (itemId == R.id.navigation_startlist) {
            fragment = new ListFragment();
            selectedTab = TabSelection.Startliste;
        } else if (itemId == R.id.navigation_liveresult) {
            fragment = new ListFragment();
            selectedTab = TabSelection.Rangliste;
        } else if (itemId == R.id.navigation_profil) {
            fragment = new ProfilFragment();
            selectedTab = TabSelection.Profil;
        } else {
            fragment = new DetailsFragment();
            selectedTab = TabSelection.Details;
        }
    }

    public final void initEvents() {
        Cursor cursor = daten.getEvents();
        events.clear();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            events.add(new Event(cursor));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void reloadEvents(ArrayList<Event> events){
        this.events = events;
        if(selectedEvent != null){
            for(int i=0;i<events.size(); i++){
                if(events.get(i).getId() == selectedEvent.getId()){
                    selectedEvent = events.get(i);
                    i = events.size();
                }
            }
        }
        fragment.reloadEvents();
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }

    public Daten getDaten() {
        return daten;
    }

    public MyHttpClient getHttpClient(){
        return httpClient;
    }

    @Override
    protected void onDestroy() {
        daten.Close();
        super.onDestroy();
    }

   public final void openEventDetails(Event e, Event.UriArt uriArt) {
        selectedEvent = e;
        switch (uriArt) {
//            case Event.UriArt.Rangliste:
//                openRangliste();
//                break;
//            case Event.UriArt.Startliste:
//                openStartliste();
//                break;
            default:
                openWebBrowser(e.getUri(uriArt));
                break;
        }
    }

    public void openWebBrowser(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    /*public final void setSelectedEvent(Event e) {
        selectedEvent = e;
    }

    public static Event getUpComingEvent(ArrayList<Event> selectedEvents) {
            Event upComingEvent = selectedEvents.Where(i -> i.Date >= LocalDateTime.Today).FirstOrDefault();
            upComingEvent = upComingEvent != null ? upComingEvent : selectedEvents.Where(i -> i.Date <= LocalDateTime.Today).LastOrDefault();
            return upComingEvent;
    }

//    public final Event[] getEventSelectionables() {
//        ArrayList<Event> selectionables = new ArrayList<Event>();
//        int middleIndex = events.indexOf(selected);
//        int beginIndex = Math.max(middleIndex - Helper.selectionablesLength / 2, 0);
//        for (int i = 0; i < Helper.selectionablesLength && beginIndex + i < events.size(); i++) {
//            selectionables.add(events.get(i + beginIndex));
//        }
//        return selectionables.toArray(new Event[0]);
//    }

    private Event getUpComingEvent() {
        return getUpComingEvent(events);
    }

    public final ArrayList<Event> getEvents() {
        return events;
    }

    public final ArrayList<Event> getEvents(String filter) {
//        ArrayList<Event> filteredEvents = new ArrayList<>();
//        Cursor cursor = daten.getFilteredEvents(filter);
//        while (cursor.Read()) {
//            int id = Helper.getInt(cursor, SQLiteHelper.COLUMN_ID);
//            filteredEvents.add(events.Where(i -> i.Id == id).FirstOrDefault());
//        }
//        return filteredEvents;
    }

    public final MyHttpClient getHttpClient() {
        return httpClient;
    }

    public final Event getSelectedEvent() {
        return selectedEvent;
    }
*/

    public enum TabSelection {
        Overview, Startliste, Rangliste, Profil, Details
    }
}