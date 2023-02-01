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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public abstract class ListFragment extends MyFragment {

    private MenuProvider menuProvider;

    private boolean showOpenInBrowserInMenu = false;

    private ListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act.setToolbarTitle(getString(isStartliste() ? R.string.startlist : R.string.rangliste) + ": " + act.getSelectedEvent().getName());
        setupMenu();

        viewModel = new ViewModelProvider(this).get(ListViewModel.class);
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), refreshing -> {
            if (refreshing) {
                refresh();
            }
        });

        viewModel.setRefreshing(true);

        if (viewModel.getSucheVisible().getValue() == null) {
            viewModel.getSucheVisible().setValue(false);
        }

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
        view.findViewById(R.id.openWebBrowser).setOnClickListener(v -> openInWebBrowser());
    }

    private void setupMenu() {
        ListFragment that = this;
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.list, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_browser) {
                    openInWebBrowser();
                    return true;
                } else if (id == R.id.menu_sorting) {
                    SortierDialog dialog = new SortierDialog(that);
                    dialog.show(act.getSupportFragmentManager(), "sorting");
                    return true;
                } else if (id == R.id.menu_refresh) {
                    refresh();
                    return true;
                } else if (id == R.id.menu_search) {
                    viewModel.toggleSuche();
                    return true;
                }
                return false;
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                menu.findItem(R.id.menu_browser).setVisible(showOpenInBrowserInMenu);
            }
        };
        act.addMenuProvider(menuProvider);

    }

    private void refresh() {
        if (!act.getParser().sendLaeuferRequest(act.getSelectedEvent().getId(), this)) {
            viewModel.setRefreshing(false);
            showFragments();
            viewModel.triggerList();
        }
    }

    @Override
    public void reloadList() {
        showFragments();
        viewModel.setRefreshing(false);
        viewModel.triggerList();
    }

    public void triggerSingleList() {
        if (viewModel != null) {
            viewModel.triggerList();
        }
    }

    private void openInWebBrowser() {
        act.openWebBrowser(getUri());
    }

    private Uri getUri() {
        return act.getSelectedEvent().getUri(isStartliste() ? Event.UriArt.Startliste : Event.UriArt.Rangliste);
    }

    public final void showFragments() {
        if (getView() != null) {
            getView().findViewById(R.id.no_list).setVisibility(View.GONE);
            getView().findViewById(R.id.tabLayout).setVisibility(View.GONE);
            getView().findViewById(R.id.viewPager).setVisibility(View.GONE);
            getView().findViewById(R.id.openWebBrowser).setVisibility(View.GONE);
            showOpenInBrowserInMenu = false;
            int count = act.getDaten().getLaeuferCountByEvent(act.getSelectedEvent(), getListType());
            if (count > 0) {
                getView().findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                showOpenInBrowserInMenu = true;
            } else if (getUri() != null) {
                getView().findViewById(R.id.openWebBrowser).setVisibility(View.VISIBLE);
                showOpenInBrowserInMenu = true;
                openInWebBrowser();
            } else {
                getView().findViewById(R.id.no_list).setVisibility(View.VISIBLE);
            }
            act.invalidateMenu();
        }
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

    public static class ListViewModel extends ViewModel {
        private final MutableLiveData<Boolean> sucheVisible = new MutableLiveData<>();
        private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
        private final MutableLiveData<Boolean> triggerList = new MutableLiveData<>();

        public void setRefreshing(boolean b) {
            if (!Objects.equals(refreshing.getValue(), b)) {
                refreshing.setValue(b);
            }
        }

        public MutableLiveData<Boolean> getRefreshing() {
            return refreshing;
        }

        public void toggleSuche() {
            if (sucheVisible.getValue() == null) {
                sucheVisible.setValue(true);
            } else {
                boolean b = sucheVisible.getValue();
                sucheVisible.setValue(!b);
            }
        }

        public MutableLiveData<Boolean> getSucheVisible() {
            return sucheVisible;
        }

        public void triggerList() {
            if (triggerList.getValue() == null) {
                triggerList.setValue(true);
            } else {
                boolean b = triggerList.getValue();
                triggerList.setValue(!b);
            }
        }

        public MutableLiveData<Boolean> getTriggerList() {
            return triggerList;
        }
    }
}
