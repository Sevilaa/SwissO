using Android.App;
using Android.OS;
using Android.Views;
using AndroidX.AppCompat.App;
using AndroidX.Preference;

namespace SwissO.Droid {

    [Activity(Label = "@string/settings", Theme = "@style/AppTheme")]
    internal class SettingsActivity : AppCompatActivity {

        protected override void OnCreate(Bundle savedInstanceState) {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_settings);
            if (savedInstanceState == null) {
                SupportFragmentManager.BeginTransaction()
                        .Replace(Resource.Id.settings_container, new SettingsFragment())
                        .Commit();
            }
            if (SupportActionBar != null) {
                SupportActionBar.SetDisplayHomeAsUpEnabled(true);
            }
        }

        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {
                case Android.Resource.Id.Home: {
                        Finish();
                        return true;
                    }
            }
            return base.OnOptionsItemSelected(item);
        }
    }

    internal class SettingsFragment : PreferenceFragmentCompat {
        public override void OnCreatePreferences(Bundle savedInstanceState, string rootKey) {
            SetPreferencesFromResource(Resource.Xml.root_preferences, rootKey);
        }
    }
}