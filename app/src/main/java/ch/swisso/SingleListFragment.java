package ch.swisso;

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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleListFragment extends Fragment {

    private final HashMap<Chip, String> chips = new HashMap<>();
    private SingleListViewModel singleViewModel;
    private ListFragment.ListViewModel listViewModel;
    private MainActivity act;
    private ListFragment listFragment;
    private ListContent listContent;
    private SwipeRefreshLayout refreshLayout;
    private TextInputEditText suche;

    private boolean sucheVisible = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_list, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MainActivity) getActivity();
        singleViewModel = new ViewModelProvider(this).get(SingleListViewModel.class);
        if (listContent == null) {
            listContent = singleViewModel.getListContent();
        } else {
            singleViewModel.setListContent(listContent);
        }
        listFragment = (ListFragment) getParentFragment();
        listViewModel = new ViewModelProvider(listFragment).get(ListFragment.ListViewModel.class);

        chips.put(view.findViewById(R.id.chip_club_list), SQLiteHelper.COLUMN_CLUB);
        chips.put(view.findViewById(R.id.chip_name_list), SQLiteHelper.COLUMN_NAME);
        chips.put(view.findViewById(R.id.chip_kategorie_list), SQLiteHelper.COLUMN_KATEGORIE);
        if (listFragment.isStartliste()) {
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
            listViewModel.setRefreshing(true);
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

        listViewModel.getRefreshing().observe(listFragment.getViewLifecycleOwner(), refreshing -> {
            if (refreshing != refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(refreshing);
            }
        });
        listViewModel.getTriggerList().observe(listFragment.getViewLifecycleOwner(), trigger -> loadList());

        listViewModel.getSucheVisible().observe(listFragment.getViewLifecycleOwner(), this::setSucheVisibility);
    }

    private void setSucheVisibility(boolean visibile) {
        if (getView() != null) {
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
                LaeuferAdapter adapter = new LaeuferAdapter(getContext(), listFragment.getListType(), laeufer);
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
        Daten daten = act.getDaten();
        ListFragment.ListType listType = listFragment.getListType();
        if (listType == ListFragment.ListType.Startliste) {
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
                if (listType == ListFragment.ListType.Startliste) {
                    order += ", " + SQLiteHelper.COLUMN_STARTNUMMER;
                } else {
                    order += ", (" + SQLiteHelper.COLUMN_ZIELZEIT + " < 0), " + SQLiteHelper.COLUMN_RANG;
                }
            }
        }

        Cursor cursor = daten.getFilteredLaeuferByEvent(act.getSelectedEvent(), listType, listContent, filter, chips, order);
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
        SharedPreferences pref = act.getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getBoolean(key, def);
    }

    public final String getStringPref(String key, String def) {
        SharedPreferences pref = act.getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getString(key, def);
    }

    public void setListContent(ListContent content) {
        listContent = content;
    }

    public enum ListContent {
        Friends,
        Club,
        alle
    }

    public static class SingleListViewModel extends ViewModel {
        private ListContent listContent;

        public ListContent getListContent() {
            return listContent;
        }

        public void setListContent(ListContent listContent) {
            this.listContent = listContent;
        }

    }
}
