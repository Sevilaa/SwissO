using Android.OS;
using Android.Views;
using Android.Widget;
using AndroidX.ViewPager2.Widget;
using Google.Android.Material.Tabs;

namespace SwissO.Droid {
    public class ListFragment : MyFragment, IListPage, TabLayoutMediator.ITabConfigurationStrategy {

        private ListManager manager;
        private ListManager.ListType listType;

        private ListFragmentPagerAdapter adapter;

        public ListFragment(MainActivity activity, ListManager.ListType listType) : base(activity, listType == ListManager.ListType.Startliste ? Resource.String.startlist : Resource.String.rangliste) {
            this.listType = listType;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            HasOptionsMenu = true;
            return inflater.Inflate(Resource.Layout.fragment_list, container, false);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            manager = new ListManager(this, act.GetAppManager(), listType);
            adapter = new ListFragmentPagerAdapter(this, manager);
            ViewPager2 viewPager = (ViewPager2)view.FindViewById(Resource.Id.viewPager);
            viewPager.Adapter = adapter;
            TabLayout tabLayout = (TabLayout)view.FindViewById(Resource.Id.tabLayout);
            new TabLayoutMediator(tabLayout, viewPager, this).Attach();
            View.FindViewById<Button>(Resource.Id.openWebBrowser).Click += (sender, e) => {
                OpenInWebBrowser();
            };
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
                        act.GetAppManager().SetEvent(adapter[e.Position]);
                        act.InvalidateOptionsMenu();
                        manager.InitEvent();
                    }
                };
            }
        }

        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {
                case Resource.Id.internet:
                    if (act.GetAppManager().GetSelected() != null) {
                        OpenInWebBrowser();
                    }
                    return true;
            }
            return base.OnOptionsItemSelected(item);
        }

        private void OpenInWebBrowser() {
            act.OpenWebBrowser(act.GetAppManager().GetSelected().Startliste);
        }

        public void UpdateList() {
            adapter.UpdateList();
            ShowList();
        }

        public void ShowNotAvailable() {
            View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Visible;
            View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
        }

        public void ShowProgressBar() {
            View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Visible;
            View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
        }

        public void ShowList() {
            View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Visible;
            View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Visible;
            View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
        }

        public void ShowOnlyInWebBrowser() {
            View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
            View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Visible;
            OpenInWebBrowser();
        }
    }
}