package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class EventListFragment extends MyFragment {

    private final HashMap<Chip, String> chips = new HashMap<>();
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private MenuProvider menuProvider;
    private MainActivity.MainViewModel actViewModel;

    private boolean sucheVisible = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMenu();
        actViewModel = new ViewModelProvider(act).get(MainActivity.MainViewModel.class);

        chips.put(view.findViewById(R.id.chip_club), SQLiteHelper.COLUMN_CLUB);
        chips.put(view.findViewById(R.id.chip_karte), SQLiteHelper.COLUMN_MAP);
        chips.put(view.findViewById(R.id.chip_name), SQLiteHelper.COLUMN_NAME);
        chips.put(view.findViewById(R.id.chip_region), SQLiteHelper.COLUMN_REGION);
        for (Chip chip : chips.keySet()) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> showList());
        }
        refreshLayout = view.findViewById(R.id.refreshLayout_overview);
        listView = view.findViewById(R.id.listView_overview);

        refreshLayout.setOnRefreshListener(() -> actViewModel.setRefreshingEvents(true));

       /* setSucheVisibility(false);
        suche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                showList();
            }
        });*/

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

        if (((MainActivity)act).getEvents().size() != 0) {
            showList();
        }
    }

    private void setupMenu() {
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.overview, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_search) {
                    //setSucheVisibility(!sucheVisible);
                    return true;
                } else if (id == R.id.menu_refresh) {
                    actViewModel.setRefreshingEvents(true);
                    return true;
                }
                return false;
            }
        };
        act.addMenuProvider(menuProvider);
    }

    public void refresh() {
        if (!act.getParser().sendEventRequest(this)) {
            actViewModel.setRefreshingEvents(false);
        }
    }

    public void reloadList() {
        ((MainActivity)act).initEvents();
        actViewModel.setRefreshingEvents(false);
    }

    public void showList() {
        MainActivity act = (MainActivity) super.act;
        if (getView() != null) {
            ArrayList<Event> events;
            if (sucheVisible) {
                events = new ArrayList<>();
                String filter = ""; // suche.getText().toString();
                Cursor cursor = act.getDaten().getEvents(filter, chips);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    events.add(new Event(cursor));
                    cursor.moveToNext();
                }
            } else {
                events = act.getEvents();
            }
            if (events.size() > 0) {
                int selectedIndex = events.indexOf(act.getSelectedEvent());
                if (selectedIndex == -1) {
                    selectedIndex = events.size() - 1;
                }
                listView.setVisibility(View.VISIBLE);
                OverviewAdapter adapter = new OverviewAdapter(act, events);
                listView.setAdapter(adapter);
                listView.setSelection(selectedIndex);
            } else {
                listView.setVisibility(View.INVISIBLE);
            }
            listView.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        act.removeMenuProvider(menuProvider);
    }
}
