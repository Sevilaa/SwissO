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
        }

        private MyCursor GetLaeuferCursor() {
            return listContent switch {
                ListContent.Friends => listManager.GetFriendsLaeufer(),
                ListContent.Club => listManager.GetClubLaeufer(),
                ListContent.alle => listManager.GetAlleLaeufer(),
                _ => null,
            };
        }

        public void LoadList() {
            MyCursor cursor = GetLaeuferCursor();
            if (cursor.Length() > 0) {
                //ExpandableListView listView = View.FindViewById<ExpandableListView>(Resource.Id.expandableListView);
                //SubListAdapter adapter = new SubListAdapter(laeufer, listManager.GetListType(), (LayoutInflater)Context.GetSystemService(Context.LayoutInflaterService));
                //listView.SetAdapter(adapter);
                //listView.Invalidate();
                ICursor cursor1 = ((MyCursor_A)cursor).Get();
                ListView listView = View.FindViewById<ListView>(Resource.Id.listview_laeufer);
                SimpleCursorAdapter adapter = null;
                if (listManager.GetListType() == ListManager.ListType.Startliste) {
                    string[] from = new string[] { SQLiteHelper.COLUMN_Startzeit, SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category };
                    int[] to = new int[] { Resource.Id.tv_starttime, Resource.Id.tv_name, Resource.Id.tv_kat };
                    adapter = new SimpleCursorAdapter(Context, Resource.Layout.sublistitem_laeufer, cursor1, from, to, CursorAdapterFlags.None);
                }
                if (listManager.GetListType() == ListManager.ListType.Rangliste) {
                    string[] from = new string[] { SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category, SQLiteHelper.COLUMN_Zielzeit };
                    int[] to = new int[] { Resource.Id.tv_name, Resource.Id.tv_kat, Resource.Id.tv_goaltime };
                    adapter = new SimpleCursorAdapter(Context, Resource.Layout.sublistitem_laeufer, cursor1, from, to, CursorAdapterFlags.None);
                }
                listView.Adapter = adapter;
            }
        }
    }
}