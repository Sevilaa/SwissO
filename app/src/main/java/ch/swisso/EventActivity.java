package ch.swisso;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Objects;

public class EventActivity extends MyActivity {

    private Event event;
    private BottomNavigationView navigation;
    private SearchBar searchBar;
    private EventViewModel viewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        int eventID = getIntent().getIntExtra(Helper.Keys.intent_event, 0);
        int navID = getIntent().getIntExtra(Helper.Keys.intent_navID, R.id.detailsFragment);
        event = daten.createEventById(eventID);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        setupSearch(viewModel);
        viewModel.getRefreshing().observe(this, refreshing -> {
            if (refreshing) {
                refresh();
            }
        });
        viewModel.getTriggerSingleList().observe(this, trigger -> {
            navigation.getMenu().findItem(R.id.ranglistFragment).setTitle(daten.isProvListe(event.getId(), false) ? R.string.liveresult : R.string.rangliste);
            navigation.getMenu().findItem(R.id.startlistFragment).setTitle(daten.isProvListe(event.getId(), true) ? R.string.teilnehmer : R.string.startlist);
        });
        viewModel.setRefreshing(true);

        navigation = findViewById(R.id.bottom_navigation_event);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_event);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        if (navigation.getSelectedItemId() != navID) {
            navController.popBackStack();
            navController.navigate(navID);
        }
    }

    private void setupSearch(@NonNull EventViewModel viewModel) {
        searchBar = findViewById(R.id.search_bar_event);
        setSupportActionBar(searchBar);
        TextView eventTitle = findViewById(R.id.event_title);
        eventTitle.setText(event.getName());
        eventTitle.setOnClickListener(v -> eventTitle.setSelected(!eventTitle.isSelected()));

        viewModel.getSearchText().observe(this, s -> {
            searchBar.setText(s);
            invalidateOptionsMenu();
        });

        SearchView searchView = findViewById(R.id.search_view_event);
        searchView.setupWithSearchBar(searchBar);

        EditText editText = searchView.getEditText();
        editText.setOnEditorActionListener((v, actionId, event) -> {
            viewModel.setSearchText(searchView.getText().toString());
            searchView.hide();
            return false;
        });

        ListView listView = findViewById(R.id.search_list_event);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String text = (String) listView.getAdapter().getItem(position);
            viewModel.setSearchText(text);
            searchView.hide();
        });
        EventActivity that = this;
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
                HashMap<String, String> suggestions = search.isEmpty() ? new HashMap<>() : daten.getLaeuferSeachSuggestions(search, event.getId(), SingleListFragment.ListContent.alle);//TODO set fav
                listView.setAdapter(new SearchListAdapter(that, suggestions));
            }
        });
    }

    public Event getEvent() {
        return event;
    }

    public void triggerSingleList() {
        if (viewModel != null) {
            viewModel.triggerSingleList();
        }
    }

    public final void openEventDetails(@NonNull Event e, @NonNull Event.UriArt uriArt) {
        switch (uriArt) {
            case Liveresultate:
            case Rangliste:
                navigation.setSelectedItemId(R.id.ranglistFragment);
                break;
            case Teilnehmerliste:
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

    private void refresh() {
        boolean hasInternet = parser.sendLaeuferRequest(event.getId(), () -> {
            viewModel.setRefreshing(false);
            viewModel.triggerSingleList();
        });
        if (!hasInternet) {
            viewModel.triggerSingleList();
            viewModel.setRefreshing(false);
        }
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public static class EventViewModel extends ViewModel {
        private final MutableLiveData<String> searchText = new MutableLiveData<>();
        private final MutableLiveData<Boolean> triggerSingleList = new MutableLiveData<>();
        private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>();

        public void setSearchText(String s) {
            searchText.setValue(s);
        }

        public MutableLiveData<String> getSearchText() {
            return searchText;
        }

        public void setRefreshing(boolean b) {
            if (!Objects.equals(refreshing.getValue(), b)) {
                refreshing.setValue(b);
            }
        }

        public MutableLiveData<Boolean> getRefreshing() {
            return refreshing;
        }

        public void triggerSingleList() {
            if (triggerSingleList.getValue() == null) {
                triggerSingleList.setValue(true);
            } else {
                boolean b = triggerSingleList.getValue();
                triggerSingleList.setValue(!b);
            }
        }

        public MutableLiveData<Boolean> getTriggerSingleList() {
            return triggerSingleList;
        }
    }
}
