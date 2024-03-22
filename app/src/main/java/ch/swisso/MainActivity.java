package ch.swisso;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends MyActivity {

    private final ArrayList<Event> events = new ArrayList<>();
    private Event selectedEvent;
    private SearchBar searchBar;
    private boolean onlyFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupSearch(viewModel);
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

    private void setupSearch(@NonNull MainViewModel viewModel) {
        searchBar = findViewById(R.id.search_bar_main);
        setSupportActionBar(searchBar);

        viewModel.getSearchText().observe(this, s -> {
            searchBar.setText(s);
            invalidateOptionsMenu();
        });

        SearchView searchView = findViewById(R.id.search_view_main);
        searchView.setupWithSearchBar(searchBar);

        EditText editText = searchView.getEditText();
        editText.setOnEditorActionListener((v, actionId, event) -> {
            viewModel.setSearchText(searchView.getText().toString());
            searchView.hide();
            return false;
        });

        ListView listView = findViewById(R.id.search_list_main);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String text = (String) listView.getAdapter().getItem(position);
            viewModel.setSearchText(text);
            searchView.hide();
        });
        MainActivity that = this;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                HashMap<String, String> suggestions = search.isEmpty() ? new HashMap<>() : daten.getEventSeachSuggestions(search, onlyFav);
                listView.setAdapter(new SearchListAdapter(that, suggestions));
            }
        });
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

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public void setOnlyFav(boolean onlyFav) {
        this.onlyFav = onlyFav;
    }

    public static class MainViewModel extends ViewModel {
        private final MutableLiveData<Boolean> refreshingEvents = new MutableLiveData<>();
        private final MutableLiveData<String> searchText = new MutableLiveData<>();

        public void setRefreshingEvents(boolean b) {
            refreshingEvents.setValue(b);
        }

        public void setSearchText(String text) {
            searchText.setValue(text);
        }

        public MutableLiveData<Boolean> getRefreshingEvents() {
            return refreshingEvents;
        }

        public MutableLiveData<String> getSearchText() {
            return searchText;
        }
    }
}