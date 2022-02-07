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

namespace SwissO.Droid {
    class SubListFragment : Fragment {

        public enum ListContent { Friends, Club, alle }

        private ListManager listManager;
        private ListContent listContent;
        private List<Laeufer> laeufer;

        public SubListFragment(ListManager listManager, ListContent listContent) : base() {
            this.listContent = listContent;
            this.listManager = listManager;

        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.Inflate(Resource.Layout.fragment_sublist, container, false);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            UpdateList();
        }

        public void UpdateList() {
            switch (listContent) {
                case ListContent.Friends:
                    laeufer = listManager.GetFriendsLaeufer();
                    break;
                case ListContent.Club:
                    laeufer = listManager.GetClubLaeufer();
                    break;
                case ListContent.alle:
                    laeufer = listManager.GetAlleLaeufer();
                    break;
            }
            LoadList();
        }

        private void LoadList() {
            if(laeufer == null) {
                laeufer = new List<Laeufer>();
                laeufer.Add(new Laeufer(null, "Severin", "Laasch", "1999", "OLG Säuliamt", "HAL", "", "12:00", "1", "1:00"));
            }
            if (laeufer != null) {
                ExpandableListView listView = View.FindViewById<ExpandableListView>(Resource.Id.expandableListView);
                SubListAdapter adapter = new SubListAdapter(laeufer, listManager.GetListType(), (LayoutInflater)Context.GetSystemService(Context.LayoutInflaterService));
                listView.SetAdapter(adapter);
                listView.Invalidate();
            }
        }
    }
}