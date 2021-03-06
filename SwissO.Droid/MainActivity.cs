using Android.App;
using Android.Content;
using Android.OS;
using Android.Views;
using AndroidX.AppCompat.App;
using Google.Android.Material.BottomNavigation;
using System;

namespace SwissO.Droid {
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity, App {

        private AppManager manager;

        private AndroidX.Fragment.App.Fragment fragment;


        protected override void OnCreate(Bundle savedInstanceState) {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            SetContentView(Resource.Layout.activity_main);

            Daten daten = new Daten_A(this);
            MyHttpClient_A httpClient = new MyHttpClient_A(this);

            manager = new AppManager(this, daten, httpClient);

            BottomNavigationView navigation = FindViewById<BottomNavigationView>(Resource.Id.navigation);
            navigation.ItemSelected += (sender, e) => {
                SupportFragmentManager.BeginTransaction().Replace(Resource.Id.host_fragment_activity_main, GetNavigatedFragment(e.Item.ItemId)).Commit();
            };

            SupportFragmentManager.BeginTransaction().Add(Resource.Id.host_fragment_activity_main, GetNavigatedFragment(navigation.SelectedItemId)).Commit();
        }

        public override bool OnCreateOptionsMenu(IMenu menu) {
            MenuInflater.Inflate(Resource.Menu.main, menu);
            return base.OnCreateOptionsMenu(menu);
        }

        //public override bool OnOptionsItemSelected(IMenuItem item) {
        //    switch (item.ItemId) {
        //        case Resource.Id.settings:
        //            Intent intent = new Intent(this, typeof(SettingsActivity));
        //            StartActivity(intent);
        //            return true;
        //        default:
        //            return base.OnOptionsItemSelected(item);
        //    }
        //}

        private AndroidX.Fragment.App.Fragment GetNavigatedFragment(int item_id) {
            switch (item_id) {
                case Resource.Id.navigation_overview:
                    fragment = new OverviewFragment();
                    return fragment;
                case Resource.Id.navigation_startlist:
                    fragment = new ListFragment(ListManager.ListType.Startliste);
                    return fragment;
                case Resource.Id.navigation_liveresult:
                    fragment = new ListFragment(ListManager.ListType.Rangliste);
                    return fragment;
                case Resource.Id.navigation_profil:
                    fragment = new ProfilFragment();
                    return fragment;
                default:
                    return null;
            }
        }

        public void OpenRangliste() {
            BottomNavigationView navigation = FindViewById<BottomNavigationView>(Resource.Id.navigation);
            navigation.SelectedItemId = Resource.Id.navigation_liveresult;
        }

        public void OpenStartliste() {
            BottomNavigationView navigation = FindViewById<BottomNavigationView>(Resource.Id.navigation);
            navigation.SelectedItemId = Resource.Id.navigation_startlist;
        }

        public void OpenWebBrowser(Uri uri) {
            if (uri != null) {
                Intent intent = new Intent(Intent.ActionView);
                intent.SetData(Android.Net.Uri.Parse(uri.ToString()));
                StartActivity(intent);
            }
        }

        public AppManager GetAppManager() {
            return manager;
        }
    }
}

