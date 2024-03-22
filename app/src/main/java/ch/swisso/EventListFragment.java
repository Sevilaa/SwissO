package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Date;

public abstract class EventListFragment extends MainFragment {

    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private TextView noFav;
    private Button showAllEvents;
    private MenuProvider menuProvider;
    private MainActivity.MainViewModel actViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMenu();
        actViewModel = new ViewModelProvider(act).get(MainActivity.MainViewModel.class);

        refreshLayout = view.findViewById(R.id.refreshLayout_overview);
        listView = view.findViewById(R.id.listView_overview);
        noFav = view.findViewById(R.id.no_fav);
        showAllEvents = view.findViewById(R.id.showallevents);

        showAllEvents.setOnClickListener(v -> act.openFragment(R.id.allEventListFragment));
        refreshLayout.setOnRefreshListener(() -> actViewModel.setRefreshingEvents(true));

        actViewModel.getRefreshingEvents().observe(act, refreshing -> {
            if (refreshing != refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(refreshing);
            }
            if (!refreshing) {
                showList();
            } else {
                refresh();
            }
        });

        actViewModel.getSearchText().observe(act, s -> showList());

        if (!act.getEvents().isEmpty()) {
            showList();
        }

        act.getSearchBar().setHint(this instanceof FavEventListFragment ? R.string.favorites : R.string.all_events);
        act.setOnlyFav(this instanceof FavEventListFragment);
    }

    private void setupMenu() {
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main, menu);
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                CharSequence cs = act.getSearchBar().getText();
                menu.findItem(R.id.menu_clearsearch).setVisible(!cs.toString().isEmpty());
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_refresh) {
                    actViewModel.setRefreshingEvents(true);
                    return true;
                }
                if (id == R.id.menu_clearsearch) {
                    actViewModel.setSearchText("");
                    return true;
                }
                return false;
            }
        };
        act.addMenuProvider(menuProvider);
    }

    public void refresh() {
        if (!act.getParser().sendEventRequest(() -> {
            act.initEvents();
            actViewModel.setRefreshingEvents(false);
        })) {
            actViewModel.setRefreshingEvents(false);
        }
    }

    public void showList() {
        if (getView() != null) {
            ArrayList<Event> events;
            String searchText = actViewModel.getSearchText().getValue();
            if (this instanceof FavEventListFragment || searchText != null && !searchText.isEmpty()) {
                events = new ArrayList<>();
                Cursor cursor = act.getDaten().getEvents(searchText, this instanceof FavEventListFragment);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    events.add(new Event(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            } else {
                events = act.getEvents();
            }
            if (!events.isEmpty()) {
                int selectedIndex = events.indexOf(act.getSelectedEvent());
                if (selectedIndex == -1) {
                    Date beginDate = act.getSelectedEvent().getBeginDate();
                    for (Event e : events) {
                        if (e.getBeginDate().after(beginDate)) {
                            selectedIndex = events.indexOf(e);
                            break;
                        }
                    }
                }
                listView.setVisibility(View.VISIBLE);
                OverviewAdapter adapter = new OverviewAdapter(act, events);
                listView.setAdapter(adapter);
                listView.setSelection(selectedIndex);
            } else {
                listView.setVisibility(View.INVISIBLE);
            }
            listView.invalidate();
            if (this instanceof FavEventListFragment) {
                Cursor c = act.getDaten().getEvents(null, true);
                int vis = c.getCount() == 0 ? View.VISIBLE : View.GONE;
                noFav.setVisibility(vis);
                showAllEvents.setVisibility(vis);
                c.close();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        act.removeMenuProvider(menuProvider);
    }
}
