package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
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

    private boolean sucheVisible = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        refreshLayout.setOnRefreshListener(this::refresh);

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
        if (act.getEvents().size() == 0) {
            refresh();
        } else {
            showList();
        }
    }

    public boolean onOptionsItemClicked(int itemId) {
        if (itemId == R.id.menu_search) {
            setSucheVisibility(!sucheVisible);
            return true;
        } else if (itemId == R.id.menu_refresh) {
            refresh();
            return true;
        }
        return false;
    }

    private void setSucheVisibility(boolean visibile) {
        sucheVisible = visibile;
        getView().findViewById(R.id.suche_overview_layout).setVisibility(visibile ? View.VISIBLE : View.GONE);
        showList();
    }

    public void refresh() {
        act.getParser().sendEventRequest();
        refreshLayout.setRefreshing(true);
    }

    public void reloadEvents() {
        showList();
        refreshLayout.setRefreshing(false);
    }

    public void reloadList() {
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


}
