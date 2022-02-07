using Android.App;
using Android.Views;
using Android.Widget;
using System;

namespace SwissO.Droid {
    class EventSpinnerAdapter : BaseAdapter<Event> {

        private readonly Activity context;
        private readonly Event[] events;

        public EventSpinnerAdapter(Event[] events, Activity context) {
            this.events = events;
            this.context = context;
        }
        public override Event this[int position] => events[position];

        public override int Count => events.Length;

        public override long GetItemId(int position) {
            return this[position].Id;
        }

        public int GetPosition(Event e) {
            return Array.IndexOf(events, e);
        }

        public override View GetView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.From(context).Inflate(Resource.Layout.spinner_actionbar, container, false);
            }
            string title = events[position].Title;
            if (title.Length > 20) {
                title = title.Substring(0, 19) + "...";
            }
            ((TextView)convertView).Text = title;
            return convertView;
        }
    }
}