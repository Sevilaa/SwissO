package ch.swisso;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleListFragment extends Fragment {

    private SingleListViewModel singleViewModel;
    private ListFragment.ListViewModel listViewModel;
    private EventActivity act;
    private ListFragment listFragment;
    private ListContent listContent;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_list, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (EventActivity) getActivity();
        singleViewModel = new ViewModelProvider(this).get(SingleListViewModel.class);
        if (listContent == null) {
            listContent = singleViewModel.getListContent();
        } else {
            singleViewModel.setListContent(listContent);
        }
        listFragment = (ListFragment) getParentFragment();
        listViewModel = new ViewModelProvider(listFragment).get(ListFragment.ListViewModel.class);

        refreshLayout = view.findViewById(R.id.refreshLayout_list);
        refreshLayout.setOnRefreshListener(() -> {
            listViewModel.setRefreshing(true);
        });

        listViewModel.getRefreshing().observe(listFragment.getViewLifecycleOwner(), refreshing -> {
            if (refreshing != refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(refreshing);
            }
        });
        listViewModel.getTriggerList().observe(listFragment.getViewLifecycleOwner(), trigger -> loadList());
    }

    public final void loadList() {
        if (getView() != null) {
            ArrayList<Laeufer> laeufer = new ArrayList<>(); //getFilteredLaeufer(chips, filter);
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

        Cursor cursor = daten.getFilteredLaeuferByEvent(act.getEvent().getId(), listType, listContent, filter, order);
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
