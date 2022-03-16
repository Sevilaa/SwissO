using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AndroidX.Fragment.App;
using Android.Database;

namespace SwissO.Droid {
    class SubListFragment : Fragment {

        public enum ListContent { Friends, Club, alle }

        private ListManager listManager;
        private ListContent listContent;
        //private List<Laeufer> laeufer;

        public SubListFragment(ListManager listManager, ListContent listContent) : base() {
            this.listContent = listContent;
            this.listManager = listManager;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.Inflate(Resource.Layout.fragment_sublist, container, false);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            LoadList();
            EditText schnellfilter = (EditText)view.FindViewById(Resource.Id.schnellfilter);
            schnellfilter.TextChanged += (sender, e) => {
                LoadList();
            };
        }

        private MyCursor GetLaeuferCursor() {
            string filter = View.FindViewById<EditText>(Resource.Id.schnellfilter).Text;
            return listContent switch {
                ListContent.Friends => listManager.GetFriendsLaeufer(filter),
                ListContent.Club => listManager.GetClubLaeufer(filter),
                ListContent.alle => listManager.GetAlleLaeufer(filter),
                _ => null,
            };
        }

        public void LoadList() {
            MyCursor cursor = GetLaeuferCursor();
            ListView listView = View.FindViewById<ListView>(Resource.Id.listview_laeufer);
            if (cursor.Length() > 0) {
                listView.Visibility = ViewStates.Visible;
                ICursor cursor1 = ((MyCursor_A)cursor).Get();
                SimpleCursorAdapter adapter = null;
                if (listManager.GetListType() == ListManager.ListType.Startliste) {
                    string[] from = new string[] { SQLiteHelper.COLUMN_Startzeit, SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category, SQLiteHelper.COLUMN_Startnummer };
                    int[] to = new int[] { Resource.Id.sl_starttime, Resource.Id.sl_name, Resource.Id.sl_kat, Resource.Id.sl_startnummer };
                    adapter = new SimpleCursorAdapter(Context, Resource.Layout.listitem_startzeit, cursor1, from, to, CursorAdapterFlags.None);
                }
                if (listManager.GetListType() == ListManager.ListType.Rangliste) {
                    string[] from = new string[] { SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category, SQLiteHelper.COLUMN_Zielzeit, SQLiteHelper.COLUMN_Rang };
                    int[] to = new int[] { Resource.Id.rl_name, Resource.Id.rl_kat, Resource.Id.rl_zielzeit, Resource.Id.rl_rang };
                    adapter = new SimpleCursorAdapter(Context, Resource.Layout.listitem_zielzeit, cursor1, from, to, CursorAdapterFlags.None);
                }
                listView.Adapter = adapter;
            }
            else {
                listView.Visibility = ViewStates.Gone;
            }
        }
    }
}