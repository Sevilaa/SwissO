package ch.swisso;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

public class EventActivity extends MyActivity {

    private Event event;
    private BottomNavigationView navigation;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        int eventID = getIntent().getIntExtra(Helper.Keys.intent_event, 0);
        int navID = getIntent().getIntExtra(Helper.Keys.intent_navID, R.id.detailsFragment);
        event = daten.createEventById(eventID);

        SearchBar searchBar = findViewById(R.id.search_bar_event);
        setSupportActionBar(searchBar);
        searchBar.setHint(event.getName());

        SearchView searchView = findViewById(R.id.search_view_event);
        searchView.setupWithSearchBar(searchBar);

        navigation = findViewById(R.id.bottom_navigation_event);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_event);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        if (navigation.getSelectedItemId() != navID) {
            navigation.setSelectedItemId(navID);
        }

        //EventActivity.EventViewModel viewModel = new ViewModelProvider(this).get(EventActivity.EventViewModel.class);
        //viewModel.setRefreshingEvent(true);
    }

    public Event getEvent() {
        return event;
    }


    public final void openEventDetails(Event e, @NonNull Event.UriArt uriArt) {
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

    /*public static class EventViewModel extends ViewModel {
        private final MutableLiveData<Boolean> refreshingEvent = new MutableLiveData<>();

        public void setRefreshingEvent(boolean b) {
            refreshingEvent.setValue(b);
        }

        public MutableLiveData<Boolean> getRefreshingEvent() {
            return refreshingEvent;
        }
    }*/
}
