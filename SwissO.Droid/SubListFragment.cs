using System;
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using System.Collections.Generic;
using AndroidX.Fragment.App;

namespace SwissO.Droid {

    public class LaeuferAdapter : ArrayAdapter<Laeufer> {

        private readonly ListManager.ListType listType;

        public LaeuferAdapter(Context context, ListManager.ListType listType, List<Laeufer> laeufer) :
            base(context, listType == ListManager.ListType.Startliste ? Resource.Layout.listitem_startzeit : Resource.Layout.listitem_zielzeit, laeufer) {
            this.listType = listType;
        }

        public override View GetView(int position, View convertView, ViewGroup parent) {
            Laeufer laeufer = GetItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                LayoutInflater inflater = LayoutInflater.From(Context);
                convertView = inflater.Inflate(listType == ListManager.ListType.Startliste ? Resource.Layout.listitem_startzeit : Resource.Layout.listitem_zielzeit, parent, false);
            }
            TextView nummer, name, kat, zeit;
            if (listType == ListManager.ListType.Startliste) {
                nummer = convertView.FindViewById<TextView>(Resource.Id.sl_startnummer);
                name = convertView.FindViewById<TextView>(Resource.Id.sl_name);
                kat = convertView.FindViewById<TextView>(Resource.Id.sl_kat);
                zeit = convertView.FindViewById<TextView>(Resource.Id.sl_starttime);
            }
            else {
                nummer = convertView.FindViewById<TextView>(Resource.Id.rl_rang);
                name = convertView.FindViewById<TextView>(Resource.Id.rl_name);
                kat = convertView.FindViewById<TextView>(Resource.Id.rl_kat);
                zeit = convertView.FindViewById<TextView>(Resource.Id.rl_zielzeit);
            }
            // Populate the data from the data object into the template view
            name.Text = laeufer.Name;
            kat.Text = laeufer.Category;
            if (listType == ListManager.ListType.Startliste) {
                nummer.Text = laeufer.Startnummer != Helper.intnull ? laeufer.Startnummer.ToString() : "";
                zeit.Text = laeufer.Startzeit == TimeSpan.MinValue ? "" : laeufer.Startzeit.ToString(@"h\:mm");
            }
            else {
                nummer.Text = laeufer.Rang != Helper.intnull ? laeufer.Rang + "." : "";
                zeit.Text = Helper.GetZielzeit(laeufer.Zielzeit, new MyResources_A(convertView.Resources));
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }

    class SubListFragment : Fragment {

        public enum ListContent { Friends, Club, alle }

        private readonly ListManager listManager;
        private readonly ListContent listContent;

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

        public void LoadList() {
            if (View != null) {
                string filter = View.FindViewById<EditText>(Resource.Id.schnellfilter).Text;
                List<Laeufer> laeufer = listContent switch {
                    ListContent.Friends => listManager.GetFriendsLaeufer(filter),
                    ListContent.Club => listManager.GetClubLaeufer(filter),
                    ListContent.alle => listManager.GetAlleLaeufer(filter),
                    _ => null,
                };
                ListView listView = View.FindViewById<ListView>(Resource.Id.listview_laeufer);
                if (laeufer.Count > 0) {
                    listView.Visibility = ViewStates.Visible;
                    LaeuferAdapter adapter = new LaeuferAdapter(Context, listManager.GetListType(), laeufer);
                    listView.Adapter = adapter;
                }
                else {
                    listView.Visibility = ViewStates.Gone;
                }
            }
        }
    }
}