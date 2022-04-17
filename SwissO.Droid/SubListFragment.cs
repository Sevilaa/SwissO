using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using System.Collections.Generic;
using AndroidX.Fragment.App;

namespace SwissO.Droid {

    public class LaeuferAdapter : ArrayAdapter<Laeufer> {

        private class ViewHolder : Java.Lang.Object{
            public TextView Nummer { get; set; }
            public TextView Name { get; set; }
            public TextView Kat { get; set; }
            public TextView Zeit { get; set; }
        }

        private readonly ListManager.ListType listType;

        public LaeuferAdapter(Context context, ListManager.ListType listType, List<Laeufer> laeufer) :
            base(context, listType == ListManager.ListType.Startliste ? Resource.Layout.listitem_startzeit : Resource.Layout.listitem_zielzeit, laeufer) {
            this.listType = listType;
        }

        public override View GetView(int position, View convertView, ViewGroup parent) {
            Laeufer laeufer = GetItem(position);
            ViewHolder viewHolder;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.From(Context);
                if (listType == ListManager.ListType.Startliste) {
                    convertView = inflater.Inflate(Resource.Layout.listitem_startzeit, parent, false);
                    viewHolder.Nummer = convertView.FindViewById<TextView>(Resource.Id.sl_startnummer);
                    viewHolder.Name = convertView.FindViewById<TextView>(Resource.Id.sl_name);
                    viewHolder.Kat = convertView.FindViewById<TextView>(Resource.Id.sl_kat);
                    viewHolder.Zeit = convertView.FindViewById<TextView>(Resource.Id.sl_starttime);
                }
                if (listType == ListManager.ListType.Rangliste) {
                    convertView = inflater.Inflate(Resource.Layout.listitem_zielzeit, parent, false);
                    viewHolder.Nummer = convertView.FindViewById<TextView>(Resource.Id.rl_rang);
                    viewHolder.Name = convertView.FindViewById<TextView>(Resource.Id.rl_name);
                    viewHolder.Kat = convertView.FindViewById<TextView>(Resource.Id.rl_kat);
                    viewHolder.Zeit = convertView.FindViewById<TextView>(Resource.Id.rl_zielzeit);
                }
                // Cache the viewHolder object inside the fresh view
                convertView.Tag = viewHolder;
            }
            else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder)convertView.Tag;

            }
            // Populate the data from the data object via the viewHolder object into the template view
            viewHolder.Name.Text = laeufer.Name;
            viewHolder.Kat.Text = laeufer.Category;
            if (listType == ListManager.ListType.Rangliste) {
                viewHolder.Nummer.Text = laeufer.Rang != Helper.intnull ? laeufer.Rang + "." : "";
                viewHolder.Zeit.Text = laeufer.Zielzeit.ToLongTimeString().TrimStart('0').TrimStart(':');
            }
            if (listType == ListManager.ListType.Startliste) {
                viewHolder.Nummer.Text = laeufer.Startnummer != Helper.intnull ? laeufer.Startnummer.ToString() : "";
                viewHolder.Zeit.Text = laeufer.Startzeit.ToShortTimeString().TrimStart('0').TrimStart(':');
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

        private List<Laeufer> GetLaeuferCursor() {
            string filter = View.FindViewById<EditText>(Resource.Id.schnellfilter).Text;
            return listContent switch {
                ListContent.Friends => listManager.GetFriendsLaeufer(filter),
                ListContent.Club => listManager.GetClubLaeufer(filter),
                ListContent.alle => listManager.GetAlleLaeufer(filter),
                _ => null,
            };
        }

        public void LoadList() {
            if (View != null) {
                List<Laeufer> laeufer = GetLaeuferCursor();
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