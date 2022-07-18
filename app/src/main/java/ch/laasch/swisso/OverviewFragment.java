package ch.laasch.swisso;

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

    private MainActivity act;

    private final HashMap<Chip, String> chips = new HashMap<>();
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private TextInputEditText suche;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MainActivity)getActivity();
        act.setTitle(R.string.overview);
        chips.put(view.findViewById(R.id.chip_club), SQLiteHelper.COLUMN_CLUB);
        chips.put(view.findViewById(R.id.chip_karte), SQLiteHelper.COLUMN_MAP);
        chips.put(view.findViewById(R.id.chip_name), SQLiteHelper.COLUMN_NAME);
        chips.put(view.findViewById(R.id.chip_region), SQLiteHelper.COLUMN_REGION);
        for (Chip chip: chips.keySet()) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showList();
            });
        }
        refreshLayout = view.findViewById(R.id.refreshLayout_overview);
        listView = view.findViewById(R.id.listView_overview);
        suche = view.findViewById(R.id.suche_overview);

        refreshLayout.setOnRefreshListener(this::refresh);

        view.findViewById(R.id.chip_group_overview).setVisibility(suche.hasFocus() ? View.VISIBLE : View.GONE);
        suche.setOnFocusChangeListener((v, hasFocus) -> {
            view.findViewById(R.id.chip_group_overview).setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });
        suche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                showList();
            }
        });
        if(act.getEvents().size() == 0){
            refresh();
        }
        else {
            showList();
        }
    }

    public void refresh(){
        SwissOParser parser = new SwissOParser(act);
        parser.sendEventRequest();
        refreshLayout.setRefreshing(true);
    }

    public void reloadEvents(){
        showList();
        refreshLayout.setRefreshing(false);
    }

    public void showList(){
        if (getView() != null) {
            String filter = suche.getText().toString();
            ArrayList<Event> events = act.getEvents();
            ArrayList<Event> filteredEvents = new ArrayList<>();
            Event topShownEvent = null;
            Cursor cursor = act.getDaten().getEvents(filter, chips);
            cursor.moveToFirst();
            for (int i = 0; i < events.size() && !cursor.isAfterLast(); i++){
                Event e = events.get(i);
                if(Helper.getInt(cursor, SQLiteHelper.COLUMN_ID) == e.getId()){
                    filteredEvents.add(e);
                    cursor.moveToNext();
                    if((act.getSelectedEvent() != null && e == act.getSelectedEvent()) ||
                            (topShownEvent == null && e.getBeginDate() != null && e.getBeginDate().getTime() >= Helper.getToday().getTimeInMillis())){
                        topShownEvent = e;
                    }
                }
            }
            if (filteredEvents.size() > 0) {
                if(topShownEvent == null){
                    topShownEvent = filteredEvents.get(filteredEvents.size() - 1);
                }
                listView.setVisibility(View.VISIBLE);
                OverviewAdapter adapter = new OverviewAdapter(act, filteredEvents);
                listView.setAdapter(adapter);
                listView.setSelection(filteredEvents.indexOf(topShownEvent));
            }
            else {
                listView.setVisibility(View.INVISIBLE);
            }
            listView.invalidate();
        }
    }


}
