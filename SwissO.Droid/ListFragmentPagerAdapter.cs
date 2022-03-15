using AndroidX.Fragment.App;
using AndroidX.ViewPager2.Adapter;
using System.Collections.Generic;

namespace SwissO.Droid {
    public class ListFragmentPagerAdapter : FragmentStateAdapter {

        private ListFragment parent;
        private ListManager listManager;

        private List<SubListFragment> subListFragments = new List<SubListFragment>();

        public ListFragmentPagerAdapter(ListFragment fragment, ListManager listManager) : base(fragment) {
            this.listManager = listManager;
            parent = fragment;
        }

        public override int ItemCount => 3;

        public override Fragment CreateFragment(int position) {
            SubListFragment.ListContent listContent = SubListFragment.ListContent.alle;
            switch (position) {
                case 0:
                    listContent = SubListFragment.ListContent.Friends;
                    break;
                case 1:
                    listContent = SubListFragment.ListContent.Club;
                    break;
                case 2:
                    listContent = SubListFragment.ListContent.alle;
                    break;
            }
            SubListFragment subListFragment = new SubListFragment(listManager, listContent);
            subListFragments.Add(subListFragment);
            return subListFragment;
        }

        public void UpdateList() {
            foreach (SubListFragment fragment in subListFragments) {
                fragment.LoadList();
            }

        }
    }
}