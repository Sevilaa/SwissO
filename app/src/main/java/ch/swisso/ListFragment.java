package ch.swisso;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public abstract class ListFragment extends EventFragment {

    private MenuProvider menuProvider;
    private EventActivity.EventViewModel eventViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMenu();

        eventViewModel = new ViewModelProvider(act).get(EventActivity.EventViewModel.class);

        ListFragmentPagerAdapter adapter = new ListFragmentPagerAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
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
    }

    private void setupMenu() {
        ListFragment that = this;
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.event, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_browser) {
                    act.openWebBrowser(getUri());
                    return true;
                }
                if (id == R.id.menu_sorting) {
                    SortierDialog dialog = new SortierDialog(that);
                    dialog.show(act.getSupportFragmentManager(), "sorting");
                    return true;
                }
                if (id == R.id.menu_clearsearch) {
                    eventViewModel.setSearchText("");
                    return true;
                }
                if (id == R.id.menu_refresh) {
                    eventViewModel.setRefreshing(true);
                    return true;
                }
                return false;
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                menu.findItem(R.id.menu_browser).setVisible(getUri() != null);
                CharSequence cs = act.getSearchBar().getText();
                menu.findItem(R.id.menu_clearsearch).setVisible(!cs.toString().isEmpty());
            }
        };
        act.addMenuProvider(menuProvider);
    }

    public Uri getUri() {
        Uri devUri = act.getEvent().getUri(isStartliste() ? Event.UriArt.Startliste : Event.UriArt.Rangliste);
        Uri provUri = act.getEvent().getUri(isStartliste() ? Event.UriArt.Teilnehmerliste : Event.UriArt.Liveresultate);
        return devUri != null ? devUri : provUri;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        act.removeMenuProvider(menuProvider);
    }

    public boolean isStartliste() {
        return getListType() == ListType.Startliste;
    }

    public boolean isRangliste() {
        return getListType() == ListType.Rangliste;
    }

    public abstract ListType getListType();

    public enum ListType {
        Startliste, Rangliste
    }
}
