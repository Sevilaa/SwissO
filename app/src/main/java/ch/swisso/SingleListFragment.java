package ch.swisso;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleListFragment extends Fragment {

    private final ListFragment listFragment;
    private final ListContent listContent;

    private final HashMap<Chip, String> chips = new HashMap<>();
    private SwipeRefreshLayout refreshLayout;
    private TextInputEditText suche;

    private boolean sucheVisible = false;

    public SingleListFragment(ListFragment listFragment, ListContent listContent) {
        this.listContent = listContent;
        this.listFragment = listFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_list, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chips.put(view.findViewById(R.id.chip_club_list), SQLiteHelper.COLUMN_CLUB);
        chips.put(view.findViewById(R.id.chip_name_list), SQLiteHelper.COLUMN_NAME);
        chips.put(view.findViewById(R.id.chip_kategorie_list), SQLiteHelper.COLUMN_KATEGORIE);
        if (listFragment.getAct().getFragmentType() == MainActivity.FragmentType.Startliste) {
            chips.put(view.findViewById(R.id.chip_startnummer_list), SQLiteHelper.COLUMN_STARTNUMMER);
            view.findViewById(R.id.chip_rang_list).setVisibility(View.GONE);
        } else {
            chips.put(view.findViewById(R.id.chip_rang_list), SQLiteHelper.COLUMN_RANG);
            view.findViewById(R.id.chip_startnummer_list).setVisibility(View.GONE);
        }
        for (Chip chip : chips.keySet()) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> loadList());
        }

        refreshLayout = view.findViewById(R.id.refreshLayout_list);
        refreshLayout.setOnRefreshListener(() -> {
            MainActivity act = listFragment.getAct();
            act.getParser().sendLaeuferRequest(act.getSelectedEvent().getId());
        });

        suche = view.findViewById(R.id.suche_list);
        suche.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                loadList();
            }
        });
        setSucheVisibility(false);
        loadList();
    }

    private void setSucheVisibility(boolean visibile) {
        if (getView() != null) {
            Activity act = listFragment.getAct();
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!visibile) {
                View v = act.getCurrentFocus();
                if (v != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            sucheVisible = visibile;
            getView().findViewById(R.id.suche_list_layout).setVisibility(visibile ? View.VISIBLE : View.GONE);
            if (visibile) {
                suche.requestFocus();
                imm.showSoftInput(suche, InputMethodManager.SHOW_IMPLICIT);
            }
            loadList();
        }
    }

    public void toggleSearch() {
        setSucheVisibility(!sucheVisible);
    }

    public void setRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }

    public final void loadList() {
        if (getView() != null) {
            String filter = null;
            if (sucheVisible) {
                filter = suche.getText().toString();
            }
            ArrayList<Laeufer> laeufer = getFilteredLaeufer(chips, filter);
            ListView listView = getView().findViewById(R.id.listView_list);
            if (!laeufer.isEmpty()) {
                listView.setVisibility(View.VISIBLE);
                LaeuferAdapter adapter = new LaeuferAdapter(getContext(), listFragment.getAct().getFragmentType(), laeufer);
                listView.setAdapter(adapter);
            } else {
                listView.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    private ArrayList<Laeufer> getFilteredLaeufer(HashMap<Chip, String> chips, String filter) {
        String column;
        String order;
        boolean ascending;
        MainActivity.FragmentType fragmentType = listFragment.getAct().getFragmentType();
        Daten daten = listFragment.getAct().getDaten();
        boolean startliste = listFragment.getAct().getFragmentType() == MainActivity.FragmentType.Startliste;
        if (startliste) {
            column = getStringPref(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
            ascending = getBoolPref(Helper.Keys.sorting_startlist_ascending, Helper.Defaults.sorting_startlist_ascending);
        } else {
            column = getStringPref(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
            ascending = getBoolPref(Helper.Keys.sorting_ranglist_ascending, Helper.Defaults.sorting_ranglist_ascending);
        }
        if (column.equals(SQLiteHelper.COLUMN_ZIELZEIT)) {
            order = "(" + SQLiteHelper.COLUMN_ZIELZEIT + " < 0), " + SQLiteHelper.COLUMN_ZIELZEIT;
        } else if (column.equals(SQLiteHelper.COLUMN_STARTNUMMER)) {
            order = column + (ascending ? " ASC" : " DESC");
        } else {
            order = column + (ascending ? " ASC" : " DESC");
            if (column.equals(SQLiteHelper.COLUMN_KATEGORIE)) {
                if (startliste) {
                    order += ", " + SQLiteHelper.COLUMN_STARTNUMMER;
                } else {
                    order += ", (" + SQLiteHelper.COLUMN_ZIELZEIT + " < 0), " + SQLiteHelper.COLUMN_RANG;
                }
            }
        }

        Cursor cursor = daten.getFilteredLaeuferByEvent(listFragment.getAct().getSelectedEvent(), fragmentType, listContent, filter, chips, order);
        ArrayList<Laeufer> laeuferList = new ArrayList<>();
        if (cursor != null) {
            ArrayList<Laeufer> nullList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (Helper.isNull(cursor, column)) {
                    nullList.add(new Laeufer(cursor));
                } else {
                    laeuferList.add(new Laeufer(cursor));
                }
                cursor.moveToNext();
            }
            laeuferList.addAll(nullList);
        }
        return laeuferList;
    }

    public final boolean getBoolPref(String key, boolean def) {
        SharedPreferences pref = getContext().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getBoolean(key, def);
    }

    public final String getStringPref(String key, String def) {
        SharedPreferences pref = getContext().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getString(key, def);
    }

    public enum ListContent {
        Friends,
        Club,
        alle
    }
}
