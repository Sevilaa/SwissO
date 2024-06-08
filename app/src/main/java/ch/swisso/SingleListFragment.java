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

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class SingleListFragment extends Fragment {

    private EventActivity.EventViewModel eventViewModel;
    private EventActivity act;
    private ListFragment listFragment;
    private String listContent;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Integer> checkedRunnerIds = new ArrayList<>();

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
        eventViewModel.getSearchParams().observe(act, s -> loadList());
        eventViewModel.getTriggerSingleList().observe(listFragment.getViewLifecycleOwner(), trigger -> {
            loadList();
            act.getSearchManager().setSeachContent(listFragment.getList());
        });

        MaterialButton openInBrowser = view.findViewById(R.id.openWebBrowser);
        openInBrowser.setOnClickListener(view1 -> act.openWebBrowser(listFragment.getUri()));
    }

    private void loadList() {
        if (getView() != null) {
            TextView noList = getView().findViewById(R.id.no_list);
            MaterialButton openInBrowser = getView().findViewById(R.id.openWebBrowser);
            ListView listView = getView().findViewById(R.id.listView_list);
            if (listFragment.getList() != null) {
                ArrayList<Runner> runners = getFilteredLaeufer();
                openInBrowser.setVisibility(View.GONE);
                if (!runners.isEmpty()) {
                    listView.setVisibility(View.VISIBLE);
                    noList.setVisibility(View.GONE);
                    RunnerAdapter adapter = new RunnerAdapter(getContext(), createConfig(runners), runners);
                    listView.setAdapter(adapter);
                } else {
                    listView.setVisibility(View.GONE);
                    noList.setVisibility(View.VISIBLE);
                    noList.setText(listContent.equals(Helper.SingleListTab.tabFreunde) ? R.string.no_friends : listContent.equals(Helper.SingleListTab.tabClub) ? R.string.no_clubs : R.string.no_kat);

                }
            } else {
                boolean hasUri = listFragment.getUri() != null;
                listView.setVisibility(View.GONE);
                noList.setVisibility(View.VISIBLE);
                openInBrowser.setVisibility(hasUri ? View.VISIBLE : View.GONE);
                noList.setText(hasUri ? R.string.listinbrowser : R.string.no_list);
            }
        }
    }

    @NonNull
    @Contract("_ -> new")
    private Config createConfig(ArrayList<Runner> runners) {
        boolean cl = getBoolPref(Helper.Keys.show_club_and_location, Helper.Defaults.show_club_and_location);
        int listType = listFragment.getList().getListType();
        if (Helper.isStartliste(listType)) {
            boolean number = false;
            for (int i = 0; i < runners.size() && !number; i++) {
                if (runners.get(i).getStartNumber() != Helper.intnull) {
                    number = true;
                }
            }
            return new Config(listType, cl, number, null);
        } else {
            return new Config(listType, cl, true, (r, b) -> checkedRunnerIds.add(r.getId()));
        }

    }

    @NonNull
    private ArrayList<Runner> getFilteredLaeufer() {
        String column;
        String order;
        boolean ascending;
        if (listFragment.getList().isStartliste()) {
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
                if (listFragment.getList().isStartliste()) {
                    order += ", " + SQLiteHelper.COLUMN_STARTNUMMER;
                } else {
                    order += ", (" + SQLiteHelper.COLUMN_ZIELZEIT + " < 0), " + SQLiteHelper.COLUMN_RANG;
                }
            }
        }

        Cursor cursor = act.getDaten().getFilteredRunnersByList(listFragment.getList().getId(), listContent, eventViewModel.getSearchParams().getValue(), order);
        ArrayList<Runner> runnerList = new ArrayList<>();
        if (cursor != null) {
            ArrayList<Runner> nullList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (Helper.isNull(cursor, column)) {
                    nullList.add(new Runner(cursor));
                } else {
                    runnerList.add(new Runner(cursor));
                }
                cursor.moveToNext();
            }
            runnerList.addAll(nullList);
        }
        return runnerList;
    }

    public final boolean getBoolPref(String key, boolean def) {
        SharedPreferences pref = act.getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getBoolean(key, def);
    }

    public final String getStringPref(String key, String def) {
        SharedPreferences pref = act.getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        return pref.getString(key, def);
    }

    public void setListContent(String content) {
        listContent = content;
    }

    public static class SingleListViewModel extends ViewModel {
        private String vmListContent;

        public String getListContent() {
            return vmListContent;
        }

        public void setListContent(String listContent) {
            this.vmListContent = listContent;
        }

    }

    public static class Config {

        public int listType;
        public boolean clubAndLocationVisible;
        public boolean numberVisible;
        public SingleRunnerLayout.OnRunnerCheckChangeListener runnerCheckChangeListener;

        public Config(int listType, boolean clubAndLocationVisible, boolean numberVisible, SingleRunnerLayout.OnRunnerCheckChangeListener runnerCheckChangeListener) {
            this.listType = listType;
            this.clubAndLocationVisible = clubAndLocationVisible;
            this.numberVisible = numberVisible;
            this.runnerCheckChangeListener = runnerCheckChangeListener;
        }
    }
}
