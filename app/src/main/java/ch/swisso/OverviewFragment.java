package ch.swisso;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class OverviewFragment extends MyFragment {

    private final HashMap<Chip, String> chips = new HashMap<>();
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private TextInputEditText suche;
    private MenuProvider menuProvider;
    private MainActivity.ActViewModel actViewModel;

    private boolean sucheVisible = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act.setToolbarTitle(getString(R.string.overview));
        setupMenu();
        actViewModel = new ViewModelProvider(act).get(MainActivity.ActViewModel.class);

        chips.put(view.findViewById(R.id.chip_club), SQLiteHelper.COLUMN_CLUB);
        chips.put(view.findViewById(R.id.chip_karte), SQLiteHelper.COLUMN_MAP);
        chips.put(view.findViewById(R.id.chip_name), SQLiteHelper.COLUMN_NAME);
        chips.put(view.findViewById(R.id.chip_region), SQLiteHelper.COLUMN_REGION);
        for (Chip chip : chips.keySet()) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> showList());
        }
        refreshLayout = view.findViewById(R.id.refreshLayout_overview);
        listView = view.findViewById(R.id.listView_overview);
        suche = view.findViewById(R.id.suche_overview);

        refreshLayout.setOnRefreshListener(() -> {
            actViewModel.setRefreshingEvents(true);
        });

        setSucheVisibility(false);
        suche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                showList();
            }
        });

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

        if (act.getEvents().size() != 0) {
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
                    setSucheVisibility(!sucheVisible);
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

    private void setSucheVisibility(boolean visibile) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!visibile) {
            View v = act.getCurrentFocus();
            if (v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        sucheVisible = visibile;
        getView().findViewById(R.id.suche_overview_layout).setVisibility(visibile ? View.VISIBLE : View.GONE);
        if (visibile) {
            suche.requestFocus();
            imm.showSoftInput(suche, InputMethodManager.SHOW_IMPLICIT);
        }
        showList();
    }

    public void refresh() {
        if (!act.getParser().sendEventRequest(this)) {
            actViewModel.setRefreshingEvents(false);
        }
    }

    public void reloadList() {
        act.initEvents();
        actViewModel.setRefreshingEvents(false);
    }

    public void showList() {
        if (getView() != null) {
            ArrayList<Event> events;
            if (sucheVisible) {
                events = new ArrayList<>();
                String filter = suche.getText().toString();
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
