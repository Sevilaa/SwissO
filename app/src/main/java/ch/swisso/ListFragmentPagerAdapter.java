package ch.swisso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ListFragmentPagerAdapter extends FragmentStateAdapter {

    public ListFragmentPagerAdapter(@NonNull ListFragment fragment) {
        super(fragment);
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
        SingleListFragment subListFragment = new SingleListFragment();
        subListFragment.setListContent(listContent);
        return subListFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
