using Android.Views;
using Android.Widget;
using System.Collections.Generic;
using System.Linq;

namespace SwissO.Droid {
    /*class SubListAdapter : BaseExpandableListAdapter {

        private List<string> categories = new List<string>();
        private Dictionary<string, List<Laeufer>> laeufer = new Dictionary<string, List<Laeufer>>();

        private ListManager.ListType listType;
        private LayoutInflater layoutInflater;

        public SubListAdapter(List<Laeufer> allLaeufer, ListManager.ListType listType, LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            this.listType = listType;
            SplitList(allLaeufer);
        }

        private void SplitList(List<Laeufer> allLaeufer) {
            foreach (Laeufer laeufer in allLaeufer) {
                if (!categories.Contains(laeufer.category)) {
                    categories.Add(laeufer.category);
                }
            }
            foreach(string cat in categories) {
                laeufer[cat] = allLaeufer.Where(laeufer => laeufer.category == cat).ToList();
            }
        } 

        public override int GroupCount => categories.Count;

        public override bool HasStableIds => false;

        public override Java.Lang.Object GetChild(int groupPosition, int childPosition) {
            string cat = categories[groupPosition];
            Laeufer l = laeufer[cat][childPosition];
            Java.Lang.String name2 = (Java.Lang.String)l.name;
            return name2;
        }

        public override long GetChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public override int GetChildrenCount(int groupPosition) {
            string cat = categories[groupPosition];
            return laeufer[cat].Count;
        }

        public override View GetChildView(int groupPosition, int childPosition, bool isLastChild, View convertView, ViewGroup parent) {
            string cat = categories[groupPosition];
            Laeufer l = laeufer[cat][childPosition];
            if (convertView == null) {
                convertView = layoutInflater.Inflate(Resource.Layout.sublistitem_laeufer, null);
            }
            TextView starttime = (TextView)convertView.FindViewById(Resource.Id.tv_starttime);
            TextView name = (TextView)convertView.FindViewById(Resource.Id.tv_name);
            TextView goaltime = (TextView)convertView.FindViewById(Resource.Id.tv_goaltime);
            switch (listType) {
                case ListManager.ListType.Startliste:
                    goaltime.Visibility = ViewStates.Gone;
                    starttime.Text = l.starttime;
                    name.Text = l.name;
                    break;
                case ListManager.ListType.Rangliste:
                    starttime.Visibility = ViewStates.Gone;
                    goaltime.Text = l.zielzeit;
                    name.Text = l.name;
                    break;
            }
            return convertView;
        }

        public override Java.Lang.Object GetGroup(int groupPosition) {
            Java.Lang.String cat2 = (Java.Lang.String)categories[groupPosition];
            return cat2;
        }

        public override long GetGroupId(int groupPosition) {
            return groupPosition;
        }

        public override View GetGroupView(int groupPosition, bool isExpanded, View convertView, ViewGroup parent) {
            string cat = categories[groupPosition];
            if(convertView == null) {
                convertView = layoutInflater.Inflate(Resource.Layout.sublistitem_group, null);
            }
            TextView title = convertView.FindViewById<TextView>(Resource.Id.tv_group);
            title.Text = cat;
            return convertView;
        }

        public override bool IsChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }*/
}