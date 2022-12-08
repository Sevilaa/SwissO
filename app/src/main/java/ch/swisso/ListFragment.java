package ch.swisso;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ListFragment extends MyFragment {

    private ListFragmentPagerAdapter adapter;
    private ViewPager2 viewPager;

    @Override
    public void reloadEvents() {
        loadList();
    }

    @Override
    public void reloadList() {
        loadList();
        adapter.setRefreshing(false, viewPager.getCurrentItem());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ListFragmentPagerAdapter(this);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TabLayoutMediator layoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
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
        });
        layoutMediator.attach();
        view.findViewById(R.id.openWebBrowser).setOnClickListener(v -> openInWebBrowser());
        int count = act.getDaten().getLaeuferCountByEvent(act.getSelectedEvent(), act.getFragmentType());
        refresh();
        if (count != 0) {
            loadList();
        }
    }

    @Override
    public boolean onOptionsItemClicked(int itemId) {
        if (itemId == R.id.menu_browser) {
            openInWebBrowser();
            return true;
        } else if (itemId == R.id.menu_sorting) {
            SortierDialog dialog = new SortierDialog(this, act);
            dialog.show(act.getSupportFragmentManager(), "sorting");
            return true;
        } else if (itemId == R.id.menu_refresh) {
            refresh();
            return true;
        } else if (itemId == R.id.menu_search) {
            adapter.toggleSearch(viewPager.getCurrentItem());
        }
        return false;
    }

    private void refresh() {
        if(act.getParser().sendLaeuferRequest(act.getSelectedEvent().getId())){
            adapter.setRefreshing(true, viewPager.getCurrentItem());
        }
    }

    private void openInWebBrowser() {
        act.openWebBrowser(getUri());
    }

    private Uri getUri() {
        return act.getFragmentType() == MainActivity.FragmentType.Startliste ? act.getSelectedEvent().getUri(Event.UriArt.Startliste) : act.getSelectedEvent().getUri(Event.UriArt.Rangliste);
    }

    public final void loadList() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).setVisibility(View.GONE);
            getView().findViewById(R.id.tabLayout).setVisibility(View.GONE);
            getView().findViewById(R.id.viewPager).setVisibility(View.GONE);
            getView().findViewById(R.id.openWebBrowser).setVisibility(View.GONE);
            boolean showOpenInBrowserInMenu = false;
            int count = act.getDaten().getLaeuferCountByEvent(act.getSelectedEvent(), act.getFragmentType());
            if (count > 0) {
                getView().findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                showOpenInBrowserInMenu = true;
                adapter.updateList();
            } else if (getUri() != null) {
                getView().findViewById(R.id.openWebBrowser).setVisibility(View.VISIBLE);
                showOpenInBrowserInMenu = true;
                openInWebBrowser();
            } else {
                getView().findViewById(R.id.no_list).setVisibility(View.VISIBLE);
            }
            act.editOptionMenuItem(R.id.menu_browser, showOpenInBrowserInMenu);
        }
    }
}
