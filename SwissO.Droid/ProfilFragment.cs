using Android.Database;
using Android.OS;
using Android.Views;
using Android.Widget;
using System;

namespace SwissO.Droid {
    public class ProfilFragment : MyFragment, IProfilPage {

        private ProfilManager manager;
        private Daten daten;

        public ProfilFragment(MainActivity activity) : base(activity, Resource.String.profil) {
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            HasOptionsMenu = true;
            return inflater.Inflate(Resource.Layout.fragment_profil, container, false);
        }

        public override void OnResume() {
            base.OnResume();
            daten = act.GetAppManager().GetDaten();
            manager = new ProfilManager(daten, this);
            (string vorname, string nachname, int si, string cat) = manager.LoadData();
            ((EditText)View.FindViewById(Resource.Id.profil_vorname)).Text = vorname;
            ((EditText)View.FindViewById(Resource.Id.profil_nachname)).Text = nachname;
            ((EditText)View.FindViewById(Resource.Id.profil_sinumber)).Text = si == Helper.intnull ? "" : "" + si;
            ((EditText)View.FindViewById(Resource.Id.profil_category)).Text = cat;
            ListView freundeList = (ListView)View.FindViewById(Resource.Id.profil_peoplelist);
            ListView clubList = (ListView)View.FindViewById(Resource.Id.profil_clublist);
            freundeList.ItemLongClick += (sender, e) => {
                int id = ((ICursor)freundeList.GetItemAtPosition(e.Position)).GetInt(0);
                daten.DeleteFreundById(id);
                ShowFriendsAndClub();
            };
            clubList.ItemLongClick += (sender, e) => {
                int id = ((ICursor)clubList.GetItemAtPosition(e.Position)).GetInt(0);
                daten.DeleteClubById(id);
                ShowFriendsAndClub();
            };

            ShowFriendsAndClub();
        }

        public override void OnStop() {
            base.OnStop();
            string vorname = ((EditText)View.FindViewById(Resource.Id.profil_vorname)).Text;
            string nachname = ((EditText)View.FindViewById(Resource.Id.profil_nachname)).Text;
            string si_string = ((EditText)View.FindViewById(Resource.Id.profil_sinumber)).Text;
            int si = si_string == "" ? Helper.intnull : Convert.ToInt32(si_string);
            string cat = ((EditText)View.FindViewById(Resource.Id.profil_category)).Text;
            manager.SaveData(vorname, nachname, si, cat);
        }

        public override void OnCreateOptionsMenu(IMenu menu, MenuInflater inflater) {
            inflater.Inflate(Resource.Menu.profil, menu);
        }

        public void ShowFriendsAndClub() {
            ListView freundeList = (ListView)View.FindViewById(Resource.Id.profil_peoplelist);
            MyCursor_A cursor = (MyCursor_A)daten.GetFreundeByProfil(manager.GetProfilID());
            if (cursor.Read()) {
                freundeList.Visibility = ViewStates.Visible;
                string[] anzeigeSpalten = new string[] { SQLiteHelper.COLUMN_Name };
                int[] anzeigeViews = new int[] { Resource.Id.listitem_name };
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(Activity, Resource.Layout.listitem_club, cursor.Get(), anzeigeSpalten, anzeigeViews, CursorAdapterFlags.None);
                freundeList.Adapter = adapter;
            }
            else {
                freundeList.Visibility = ViewStates.Invisible;
            }
            ListView clubList = (ListView)View.FindViewById(Resource.Id.profil_clublist);
            cursor = (MyCursor_A)daten.GetClubsByProfil(manager.GetProfilID());
            if (cursor.Read()) {
                clubList.Visibility = ViewStates.Visible;
                string[] anzeigeSpalten = new string[] { SQLiteHelper.COLUMN_Name };
                int[] anzeigeViews = new int[] { Resource.Id.listitem_name };
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(Activity, Resource.Layout.listitem_club, cursor.Get(), anzeigeSpalten, anzeigeViews, 0);
                clubList.Adapter = adapter;
            }
            else {
                clubList.Visibility = ViewStates.Invisible;
            }
        }


        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {

                case Resource.Id.add_friend:
                    new EditTextDialog(false, manager).Show(ChildFragmentManager, "friend");
                    return true;
                case Resource.Id.add_club:
                    new EditTextDialog(true, manager).Show(ChildFragmentManager, "friend");
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
}