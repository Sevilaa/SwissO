package ch.swisso;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SingleListFragment extends Fragment {

    private EventActivity.EventViewModel eventViewModel;
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
        eventViewModel = new ViewModelProvider(act).get(EventActivity.EventViewModel.class);

        SingleListViewModel singleViewModel = new ViewModelProvider(this).get(SingleListViewModel.class);
        if (listContent == null) {
            listContent = singleViewModel.getListContent();
        } else {
            singleViewModel.setListContent(listContent);
        }
        listFragment = (ListFragment) getParentFragment();

        refreshLayout = view.findViewById(R.id.refreshLayout_list);
        refreshLayout.setOnRefreshListener(() -> {
            eventViewModel.setRefreshing(true);
        });

        eventViewModel.getRefreshing().observe(listFragment.getViewLifecycleOwner(), refreshing -> {
            if (refreshing != refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(refreshing);
            }
        });
        eventViewModel.getSearchText().observe(act, s -> loadList());
        eventViewModel.getTriggerSingleList().observe(listFragment.getViewLifecycleOwner(), trigger -> loadList());

        MaterialButton openInBrowser = view.findViewById(R.id.openWebBrowser);
        openInBrowser.setOnClickListener(view1 -> act.openWebBrowser(listFragment.getUri()));
    }

    private void loadList() {
        if (getView() != null) {
            TextView noList = getView().findViewById(R.id.no_list);
            MaterialButton openInBrowser = getView().findViewById(R.id.openWebBrowser);
            int alle = act.getDaten().getLaeuferCountByEvent(act.getEvent().getId(), listFragment.getListType());
            ListView listView = getView().findViewById(R.id.listView_list);
            if (alle > 0) {
                ArrayList<Laeufer> laeufer = getFilteredLaeufer();
                openInBrowser.setVisibility(View.GONE);
                if (!laeufer.isEmpty()) {
                    listView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    LaeuferAdapter adapter = new LaeuferAdapter(getContext(), listFragment.getListType(), laeufer);
                    listView.setAdapter(adapter);
                } else {
                    listView.setVisibility(View.GONE);
                    noList.setVisibility(View.VISIBLE);
                    noList.setText(listContent == ListContent.Friends ? R.string.no_friends : R.string.no_clubs);

                }
            } else{
                boolean hasUri = listFragment.getUri() != null;
                listView.setVisibility(View.GONE);
                noList.setVisibility(View.VISIBLE);
                openInBrowser.setVisibility(hasUri ? View.VISIBLE : View.GONE);
                noList.setText(hasUri ? R.string.listinbrowser : R.string.no_list);
            }
        }
    }

    @NonNull
    private ArrayList<Laeufer> getFilteredLaeufer() {
        String column;
        String order;
        boolean ascending;
        if (listFragment.isStartliste()) {
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
                if (listFragment.isStartliste()) {
                    order += ", " + SQLiteHelper.COLUMN_STARTNUMMER;
                } else {
                    order += ", (" + SQLiteHelper.COLUMN_ZIELZEIT + " < 0), " + SQLiteHelper.COLUMN_RANG;
                }
            }
        }

        String filter = eventViewModel.getSearchText().getValue();
        Cursor cursor = act.getDaten().getFilteredLaeuferByEvent(act.getEvent().getId(), listFragment.getListType(), listContent, filter, order);
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
