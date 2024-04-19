package ch.swisso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import ch.swisso.SearchManager.RunnerSearchManager;

public class EventActivity extends MyActivity {

    private Event event;
    private BottomNavigationView navigation;
    private RunnerSearchManager searchManager;
    private EventViewModel viewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        int eventID = intent.getIntExtra(Helper.Keys.intent_event, 0);
        int navID = intent.getIntExtra(Helper.Keys.intent_navID, R.id.detailsFragment);

        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            String s = appLinkData.getQueryParameter(Helper.Keys.query_event_details);
            if (s != null) {
                eventID = Integer.parseInt(s);
            }
        }

        event = daten.createEventById(eventID);

        TextView eventTitle = findViewById(R.id.event_title);
        eventTitle.setText(event.getName());
        eventTitle.setOnClickListener(v -> eventTitle.setSelected(!eventTitle.isSelected()));

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        viewModel.getRefreshing().observe(this, refreshing -> {
            if (refreshing) {
                refresh();
            }
        });
        viewModel.getTriggerSingleList().observe(this, trigger -> {
            navigation.getMenu().findItem(R.id.ranglistFragment).setTitle(event.getRanglistTitle());
            navigation.getMenu().findItem(R.id.startlistFragment).setTitle(event.getStartlistTitle());
        });
        viewModel.setRefreshing(true);

        searchManager = new RunnerSearchManager(this, viewModel);

        navigation = findViewById(R.id.bottom_navigation_event);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_event);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        if (navigation.getSelectedItemId() != navID) {
            navController.popBackStack();
            navController.navigate(navID);
        }

        View.OnClickListener favOnClickListener = v -> {
            toggleFav(event);
            findViewById(R.id.details_fav_checkbox_enabled).setVisibility(event.isFavorit() ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.details_fav_checkbox_disabled).setVisibility(event.isFavorit() ? View.INVISIBLE : View.VISIBLE);
        };

        findViewById(R.id.details_fav_checkbox_enabled).setOnClickListener(favOnClickListener);
        findViewById(R.id.details_fav_checkbox_disabled).setOnClickListener(favOnClickListener);
        findViewById(R.id.details_fav_checkbox_enabled).setVisibility(event.isFavorit() ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.details_fav_checkbox_disabled).setVisibility(event.isFavorit() ? View.INVISIBLE : View.VISIBLE);
    }

    public Event getEvent() {
        return event;
    }

    public void sortingChanged() {
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
        boolean hasInternet = parser.sendEventDetailsRequest(event.getId(), () -> {
            event.initLists(daten);
            viewModel.setRefreshing(false);
            viewModel.triggerSingleList();
        });
        if (!hasInternet) {
            viewModel.triggerSingleList();
            viewModel.setRefreshing(false);
        }
    }

    public RunnerSearchManager getSearchManager() {
        return searchManager;
    }

    public static class EventViewModel extends MyViewModel {
        private final MutableLiveData<Boolean> triggerSingleList = new MutableLiveData<>();
        private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>();

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
