package ch.laasch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class ListFragment extends MyFragment {

    private MainActivity act;

    //private ListFragmentPagerAdapter adapter;

    private boolean showOpenInBrowserInMenu = true;

    @Override
    public void reloadEvents() {

    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MainActivity)getActivity();
        act.setTitle(listType == ListType.Startliste ? R.string.startlist : R.string.rangliste);

        adapter = new ListFragmentPagerAdapter(this, manager);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        viewPager.Adapter = adapter;
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        (new TabLayoutMediator(tabLayout, viewPager, this)).Attach();
        view.findViewById(R.id.openWebBrowser).Click += (sender, e) -> {
            openInWebBrowser();
        };
        initEvent();
    }

    public final void onConfigureTab(TabLayout.Tab tab, int position) {
        switch (position) {
            case 0:
                tab.setText(R.string.friends);
                break;
            case 1:
                tab.setText(R.string.club);
                break;
            case 2:
                tab.setText(R.string.alle);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem spinnerItem = menu.FindItem(R.id.spinner);
        boolean tempVar = spinnerItem.ActionView instanceof Spinner;
        Spinner spinner = tempVar ? (Spinner)spinnerItem.ActionView : null;
        if (tempVar) {
            EventSpinnerAdapter adapter = new EventSpinnerAdapter(act.GetAppManager().GetEventSelectionables(), act);
            spinner.Adapter = adapter;
            spinner.SetSelection(adapter.GetPosition(act.GetAppManager().GetSelected()));
            spinner.ItemSelected += (sender, e) -> {
                Event newSelected = adapter[e.Position];
                if (newSelected != act.GetAppManager().GetSelected()) {
                    act.GetAppManager().SetEvent(adapter[e.Position]);
                    act.invalidateOptionsMenu();
                    manager.InitEvent();
                }
            };
        }
        menu.FindItem(R.id.internet).SetVisible(showOpenInBrowserInMenu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.internet:
                if (act.GetSelected() != null) {
                    openInWebBrowser();
                }
                return true;
            case R.id.sorting:
                SortingDialog dialog = new SortingDialog(manager);
                dialog.Show(act.SupportFragmentManager, "sorting");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openInWebBrowser() {
        act.openWebBrowser(listType == ListType.Startliste ? act.GetAppManager().GetSelected().Startliste : act.GetAppManager().GetSelected().Rangliste);
    }

    public final void updateList() {
        showList();
        adapter.updateList();
    }

    public final void showNotAvailable() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).Visibility = View.VISIBLE;
            getView().findViewById(R.id.tabLayout).Visibility = View.GONE;
            getView().findViewById(R.id.viewPager).Visibility = View.GONE;
            getView().findViewById(R.id.list_progressBar).Visibility = View.GONE;
            getView().findViewById(R.id.openWebBrowser).Visibility = View.GONE;
            showOpenInBrowserInMenu = false;
            act.invalidateOptionsMenu();
        }
    }

    public final void showProgressBar() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).Visibility = View.GONE;
            getView().findViewById(R.id.tabLayout).Visibility = View.GONE;
            getView().findViewById(R.id.viewPager).Visibility = View.GONE;
            getView().findViewById(R.id.list_progressBar).Visibility = View.VISIBLE;
            getView().findViewById(R.id.openWebBrowser).Visibility = View.GONE;
        }
    }

    public final void showList() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).Visibility = View.GONE;
            getView().findViewById(R.id.tabLayout).Visibility = View.VISIBLE;
            getView().findViewById(R.id.viewPager).Visibility = View.VISIBLE;
            getView().findViewById(R.id.list_progressBar).Visibility = View.GONE;
            getView().findViewById(R.id.openWebBrowser).Visibility = View.GONE;
            showOpenInBrowserInMenu = true;
            act.invalidateOptionsMenu();
        }
    }

    public final void showOnlyInWebBrowser() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).Visibility = View.GONE;
            getView().findViewById(R.id.tabLayout).Visibility = View.GONE;
            getView().findViewById(R.id.viewPager).Visibility = View.GONE;
            getView().findViewById(R.id.list_progressBar).Visibility = View.GONE;
            getView().findViewById(R.id.openWebBrowser).Visibility = View.VISIBLE;
            showOpenInBrowserInMenu = true;
            act.invalidateOptionsMenu();
            openInWebBrowser();
        }
    }

    public final boolean getBoolPref(String key, boolean def) {
        ISharedPreferences pref = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private);
        return pref.GetBoolean(key, def);
    }

    public final String getStringPref(String key, String def) {
        ISharedPreferences pref = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private);
        return pref.GetString(key, def);
    }

    //TODO ölakjsdfölajsdfölajksdfölkjasdöflkjasölkdjfasöldkjfasölkjhdföalksjldhf

    public final void initEvent() {
        if (listType == ListType.Startliste) {
            sendStartlisteRequest();
        }
        else {
            sendRanglisteRequest();
        }

    }

    public final void sendStartlisteRequest() {
        Event selected = appManager.GetSelected();
        if (selected != null && selected.Startliste != null) {
            page.ShowProgressBar();
            if (selected.Startliste.OriginalString.Contains("entry.picoevents.ch")) {
                PicoParser picoParser = new PicoParser(httpClient, this, Parser.Parser.RequestCodes.PicoStartliste);
                picoParser.StartStartlisteRequest(selected);
            }
            else if (selected.Startliste.OriginalString.Contains("o-l.ch/cgi-bin/results?type=start&")) {
                SOLVParser solvParser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVStartliste);
                solvParser.StartStartlisteRequest(selected);
            }
            else {
                page.ShowOnlyInWebBrowser();
            }
        }
        else {
            page.ShowNotAvailable();
        }
    }

    public final void sendRanglisteRequest() {
        Event selected = act.getSelectedEvent();
        if (selected != null && selected.getUri(Event.UriArt.Rangliste) != null) {
            SOLVParser parser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVRangliste);
            parser.StartRanglisteRequest(selected);
            showProgressBar();
        }
        else {
            showNotAvailable();
        }
    }

    public final ArrayList<Laeufer> getAlleLaeufer(String filter) {
        (string order, string orderColumn) = OrderString();
        Cursor cursor = act.getDaten().getAllLaeuferByEvent(act.getSelectedEvent(), filter, order);
        return createLaeufer(cursor, orderColumn);
    }

    public final ArrayList<Laeufer> getClubLaeufer(String filter) {
        (string order, string orderColumn) = OrderString();
        Cursor cursor = act.getDaten().getClubLaeuferByEvent(act.getSelectedEvent(), profil.GetClubs(), filter, order);
        return createLaeufer(cursor, orderColumn);
    }

    public final ArrayList<Laeufer> getFriendsLaeufer(String filter) {
        (string order, string orderColumn) = OrderString();
        Cursor cursor = act.getDaten().GetFriendLaeuferByEvent(act.getSelectedEvent(), profil.GetFriends(), filter, order);
        return createLaeufer(cursor, orderColumn);
    }

    private ArrayList<Laeufer> createLaeufer(Cursor cursor, String orderColumn) {
        ArrayList<Laeufer> laeuferList = new ArrayList<>();
        ArrayList<Laeufer> nullList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (orderColumn == null) {
                laeuferList.add(new Laeufer(cursor));
            }
            else if (Helper.isNull(cursor, orderColumn)) {
                nullList.add(new Laeufer(cursor));
            }
            else {
                laeuferList.add(new Laeufer(cursor));
            }
            cursor.moveToNext();
        }
        laeuferList.addAll(nullList);
        return laeuferList;
    }

		private (string, string) orderString()
		{
			string column;
			bool ascending;
			if (listType == ListType.Startliste)
			{
				column = page.GetStringPref(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
				ascending = page.GetBoolPref(Helper.Keys.sorting_startlist_ascending, Helper.Defaults.sorting_startlist_ascending);
			}
			else
			{
				column = page.GetStringPref(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
				ascending = page.GetBoolPref(Helper.Keys.sorting_ranglist_ascending, Helper.Defaults.sorting_ranglist_ascending);
			}
			if (column == Helper.original)
			{
				return (null, null);
			}
			string order = column + (ascending ? " ASC;" : " DESC;");
			return (order, column);
		}

        private String getOrderString()

    public final void loadList() {
        int count = appManager.GetDaten().GetLaeuferCountByEvent(appManager.GetSelected());
        if (count > 0) {
            page.UpdateList();
        }
        else if (appManager.GetSelected().Startliste == null) {
            page.ShowNotAvailable();
        }
        else {
            page.ShowOnlyInWebBrowser();
        }
    }*/
}
