using Android.App;
using Android.Views;
using Android.Widget;
using System;
using System.Collections.Generic;

namespace SwissO.Droid {

    class OverviewAdapter : BaseAdapter<Event> {

        private readonly Activity context;
        private readonly List<Event> events;
        private readonly AppManager appManager;

        private OverviewLayout selected;

        public OverviewAdapter(List<Event> events, Activity context, AppManager appManager) {
            this.events = events;
            this.context = context;
            this.appManager = appManager;
            selected = null;
        }
        public override Event this[int position] => events[position];

        public override int Count => events.Count;

        public override long GetItemId(int position) {
            return events[position].Id;
        }

        public override View GetView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.From(context).Inflate(Resource.Layout.listitem_overview, container, false);
                ((OverviewLayout)convertView).SetExpandViewClick();
            }
            Event element = events[position];
            ((OverviewLayout)convertView).Init(element, appManager, this);
            return convertView;
        }

        public void ExpandViewClick(OverviewLayout v) {
            if (v == selected) {
                v.Collapse();
                selected = null;
            }
            else {
                if (selected != null) {
                    selected.Collapse();
                }
                v.Expand();
                selected = v;
            }
        }
    }
}