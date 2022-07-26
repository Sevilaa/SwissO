package ch.laasch.swisso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ListFragmentPagerAdapter extends FragmentStateAdapter {

    private final ListFragment listFragment;

    private final SingleListFragment[] singleListFragments = new SingleListFragment[3];

    public ListFragmentPagerAdapter(@NonNull ListFragment fragment) {
        super(fragment);
        listFragment = fragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        SingleListFragment.ListContent listContent;
        switch (position) {
            case 0:
                listContent = SingleListFragment.ListContent.Friends;
                break;
            case 1:
                listContent = SingleListFragment.ListContent.Club;
                break;
            case 2:
            default:
                listContent = SingleListFragment.ListContent.alle;
                break;
        }
        SingleListFragment subListFragment = new SingleListFragment(listFragment, listContent);
        singleListFragments[position] = subListFragment;
        return subListFragment;
    }

    public void updateList() {
        for (SingleListFragment fragment : singleListFragments) {
            if (fragment != null) {
                fragment.loadList();
            }
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setRefreshing(boolean b, int position) {
        if (singleListFragments[position] != null) {
            singleListFragments[position].setRefreshing(b);
        }
    }

    public void toggleSearch(int position) {
        if (singleListFragments[position] != null) {
            singleListFragments[position].toggleSearch();
        }
    }
}
