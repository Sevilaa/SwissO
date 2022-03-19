using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using AndroidX.Fragment.App;
using AndroidX.ViewPager2.Widget;
using Google.Android.Material.Tabs;

namespace SwissO.Droid {
    public class ListFragment : Fragment, IListPage, TabLayoutMediator.ITabConfigurationStrategy {

        private ListManager manager;
        private MainActivity act;
        private readonly ListManager.ListType listType;

        private ListFragmentPagerAdapter adapter;

        private bool showOpenInBrowserInMenu = true;

        public ListFragment(ListManager.ListType listType) {
            this.listType = listType;
        }

        public ListFragment() {
            listType = ListManager.ListType.Rangliste;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            HasOptionsMenu = true;
            return inflater.Inflate(Resource.Layout.fragment_list, container, false);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            act = (MainActivity)Activity;
            act.SetTitle(listType == ListManager.ListType.Startliste ? Resource.String.startlist : Resource.String.rangliste);
            manager = new ListManager(this, act.GetAppManager(), listType);
            adapter = new ListFragmentPagerAdapter(this, manager);
            ViewPager2 viewPager = (ViewPager2)view.FindViewById(Resource.Id.viewPager);
            viewPager.Adapter = adapter;
            TabLayout tabLayout = (TabLayout)view.FindViewById(Resource.Id.tabLayout);
            new TabLayoutMediator(tabLayout, viewPager, this).Attach();
            View.FindViewById<Button>(Resource.Id.openWebBrowser).Click += (sender, e) => {
                OpenInWebBrowser();
            };
            manager.InitEvent();
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
            menu.FindItem(Resource.Id.internet).SetVisible(showOpenInBrowserInMenu);
        }

        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {
                case Resource.Id.internet:
                    if (act.GetAppManager().GetSelected() != null) {
                        OpenInWebBrowser();
                    }
                    return true;
                case Resource.Id.sorting:
                    SortingDialog dialog = new SortingDialog(manager);
                    dialog.Show(act.SupportFragmentManager, "sorting");
                    return true;
            }
            return base.OnOptionsItemSelected(item);
        }

        private void OpenInWebBrowser() {
            act.OpenWebBrowser(manager.GetListType() == ListManager.ListType.Startliste ? act.GetAppManager().GetSelected().Startliste : act.GetAppManager().GetSelected().Rangliste);
        }

        public void UpdateList() {
            ShowList();
            adapter.UpdateList();
        }

        public void ShowNotAvailable() {
            if (View != null) {
                View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Visible;
                View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
                showOpenInBrowserInMenu = false;
                act.InvalidateOptionsMenu();
            }
        }

        public void ShowProgressBar() {
            if (View != null) {
                View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Visible;
                View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
            }
        }

        public void ShowList() {
            if (View != null) {
                View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Visible;
                View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Visible;
                View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Gone;
                showOpenInBrowserInMenu = true;
                act.InvalidateOptionsMenu();
            }
        }

        public void ShowOnlyInWebBrowser() {
            if (View != null) {
                View.FindViewById(Resource.Id.no_list).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.tabLayout).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.viewPager).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.list_progressBar).Visibility = ViewStates.Gone;
                View.FindViewById(Resource.Id.openWebBrowser).Visibility = ViewStates.Visible;
                showOpenInBrowserInMenu = true;
                act.InvalidateOptionsMenu();
                OpenInWebBrowser();
            }
        }

        public bool GetBoolPref(string key, bool def) {
            ISharedPreferences pref = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private);
            return pref.GetBoolean(key, def);
        }

        public string GetStringPref(string key, string def) {
            ISharedPreferences pref = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private);
            return pref.GetString(key, def);
        }
    }
}