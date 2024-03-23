package ch.swisso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ListFragmentPagerAdapter extends FragmentStateAdapter {

    private final String[] tabs;

    public ListFragmentPagerAdapter(@NonNull ListFragment fragment, String[] tabs) {
        super(fragment);
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        SingleListFragment subListFragment = new SingleListFragment();
        subListFragment.setListContent(tabs[position]);
        return subListFragment;
    }

    @Override
    public int getItemCount() {
        return tabs.length;
    }
}
