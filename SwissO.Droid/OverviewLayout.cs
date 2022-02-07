using Android.Content;
using Android.Util;
using Android.Views;
using Android.Widget;
using System;
using System.Linq;
using static SwissO.Event;
using static SwissO.MyResources;

namespace SwissO.Droid {
    class OverviewLayout : FrameLayout {

        private Event eventt;

        private OverviewAdapter adapter;

        private ImageView expandView;
        private OverviewButton[] btnAlways;
        private TextView[] tvAlways;
        private OverviewButton[] btnExpand;
        private TextView[] tvExpand;
        public OverviewLayout(Context context, IAttributeSet attrs) : base(context, attrs) {
        }

        public void Init(Event e, AppManager appManager, OverviewAdapter adapter) {
            eventt = e;
            this.adapter = adapter;

            TextView tvtitle = (TextView)FindViewById(Resource.Id.overview_item_title);
            TextView tvdate = (TextView)FindViewById(Resource.Id.overview_item_date);
            TextView tvmap = (TextView)FindViewById(Resource.Id.overview_item_map);
            TextView tvclub = (TextView)FindViewById(Resource.Id.overview_item_club);
            TextView tvdeadline = (TextView)FindViewById(Resource.Id.overview_item_deadline);
            TextView tvregion = (TextView)FindViewById(Resource.Id.overview_item_region);
            tvAlways = new TextView[] { tvtitle, tvdate, tvmap, tvclub, tvdeadline };
            tvExpand = new TextView[] { tvregion };

            tvtitle.Text = " " + eventt.Title;
            tvdate.Text = " " + GetDateString(eventt.Date);
            tvmap.Text = " " + eventt.Map;
            tvclub.Text = " " + eventt.Club;
            tvdeadline.Text = " " + GetDateString(eventt.Deadline);
            tvregion.Text = " " + eventt.Region;
            foreach (TextView tv in tvAlways) {
                SetTextViewVisible(tv);
            }
            foreach (TextView tv in tvExpand) {
                SetTextViewVisible(tv);
            }

            (UriArt[] uris, StringResource[] resources) = OverviewManager.GetUrisSorted(eventt);

            btnAlways = new OverviewButton[4];
            btnAlways[0] = (OverviewButton)FindViewById(Resource.Id.overview_item_button1);
            btnAlways[1] = (OverviewButton)FindViewById(Resource.Id.overview_item_button2);
            btnAlways[2] = (OverviewButton)FindViewById(Resource.Id.overview_item_button3);
            btnAlways[3] = (OverviewButton)FindViewById(Resource.Id.overview_item_button4);
            btnExpand = new OverviewButton[4];
            btnExpand[0] = (OverviewButton)FindViewById(Resource.Id.overview_item_button5);
            btnExpand[1] = (OverviewButton)FindViewById(Resource.Id.overview_item_button6);
            btnExpand[2] = (OverviewButton)FindViewById(Resource.Id.overview_item_button7);
            btnExpand[3] = (OverviewButton)FindViewById(Resource.Id.overview_item_button8);

            OverviewButton[] btns = btnAlways.Concat(btnExpand).ToArray();

            int btni = 0;
            int conti = 0;
            while (conti < uris.Length) {
                if (e.GetUri(uris[conti]) != null) {
                    btns[btni].Init(appManager, resources[conti], e, uris[conti]);
                    btni++;
                }
                conti++;
            }
            while (btni < btns.Length) {
                btns[btni].Init(null, 0, null, 0);
                btni++;
            }
            Collapse();
        }

        internal void SetExpandViewClick() {
            expandView = (ImageView)FindViewById(Resource.Id.overview_item_expand);
            expandView.Click += (e, sender) => {
                adapter.ExpandViewClick(this);
            };
        }

        public void Expand() {
            expandView.SetImageResource(Resource.Drawable.ic_arrow_collapse);
            foreach (OverviewButton btn in btnExpand) {
                btn.SetVisible();
            }
            foreach (TextView tv in tvExpand) {
                SetTextViewVisible(tv);
            }
        }

        public void Collapse() {
            expandView.SetImageResource(Resource.Drawable.ic_arrow_expand);
            foreach (OverviewButton btn in btnExpand) {
                btn.Visibility = ViewStates.Gone;
            }
            foreach (TextView tv in tvExpand) {
                tv.Visibility = ViewStates.Gone;
            }
        }

        private string GetDateString(DateTime value) {
            return DateTime.Compare(value, DateTime.MinValue) == 0 ? "" : value.ToLongDateString();
        }

        private void SetTextViewVisible(TextView tv) {
            tv.Visibility = string.IsNullOrWhiteSpace(tv.Text) ? ViewStates.Gone : ViewStates.Visible;
        }
    }
}