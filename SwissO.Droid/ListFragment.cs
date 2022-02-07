using Android.OS;
using Android.Views;
using Android.Widget;
using AndroidX.ViewPager2.Widget;
using Google.Android.Material.Tabs;

namespace SwissO.Droid {
    public class ListFragment : MyFragment, IListPage, TabLayoutMediator.ITabConfigurationStrategy {

        private ListManager manager;

        private ListFragmentPagerAdapter adapter;

        public ListFragment(MainActivity activity, ListManager.ListType listType) : base(activity, listType == ListManager.ListType.Startliste ? Resource.String.startlist : Resource.String.rangliste) {
            manager = new ListManager(this, act.GetAppManager(), listType);
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            HasOptionsMenu = true;
            return inflater.Inflate(Resource.Layout.fragment_list, container, false);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            adapter = new ListFragmentPagerAdapter(this, manager);
            ViewPager2 viewPager = (ViewPager2)view.FindViewById(Resource.Id.viewPager);
            viewPager.Adapter = adapter;
            TabLayout tabLayout = (TabLayout)view.FindViewById(Resource.Id.tabLayout);
            new TabLayoutMediator(tabLayout, viewPager, this).Attach();
        }

        public void OnConfigureTab(TabLayout.Tab tab, int position) {
            switch (position) {
                case 0:
                    tab.SetText(Resources.GetString(Resource.String.friends));
                    break;
                case 1:
                    tab.SetText(Resources.GetString(Resource.String.club));
                    break;
                case 2:
                    tab.SetText(Resources.GetString(Resource.String.alle));
                    break;
            }
        }

        public override void OnCreateOptionsMenu(IMenu menu, MenuInflater inflater) {
            inflater.Inflate(Resource.Menu.list, menu);

        }

        public override void OnPrepareOptionsMenu(IMenu menu) {
            IMenuItem spinnerItem = menu.FindItem(Resource.Id.spinner);
            if (spinnerItem.ActionView is Spinner spinner) {
                EventSpinnerAdapter adapter = new EventSpinnerAdapter(act.GetAppManager().GetEventSelectionables(), act);
                spinner.Adapter = adapter;
                spinner.SetSelection(adapter.GetPosition(act.GetAppManager().GetSelected()));
                spinner.ItemSelected += (sender, e) => {
                    Event newSelected = adapter[e.Position];
                    if (newSelected != act.GetAppManager().GetSelected()) {
                        act.GetAppManager().UpdateSelected(adapter[e.Position]);
                    }
                };
            }
        }

        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {
                case Resource.Id.internet:
                    if (act.GetAppManager().GetSelected() != null) {
                        act.OpenWebBrowser(act.GetAppManager().GetSelected().Startliste);
                    }
                    return true;
            }
            return base.OnOptionsItemSelected(item);
        }

        public void UpdateList() {
            adapter.UpdateList();
        }

        public override void Update() {
            act.InvalidateOptionsMenu();
            if (act.GetAppManager().GetSelected().Startliste != null) {
                manager.SendStartlisteRequest();//TODO if Startliste == null
            }
        }
    }
}