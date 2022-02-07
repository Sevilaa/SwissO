using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using AndroidX.SwipeRefreshLayout.Widget;
using System;
using System.Collections.Generic;

namespace SwissO.Droid {

    public class OverviewFragment : MyFragment, OverviewPage {

        private OverviewManager manager;

        private SwipeRefreshLayout refreshLayout;

        public OverviewFragment(MainActivity activity) : base(activity, Resource.String.overview) {
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            HasOptionsMenu = true;
            return inflater.Inflate(Resource.Layout.fragment_overview, container, false);
        }

        public override void OnCreateOptionsMenu(IMenu menu, MenuInflater inflater) {
            inflater.Inflate(Resource.Menu.overview, menu);
        }

        public override bool OnOptionsItemSelected(IMenuItem item) {
            switch (item.ItemId) {
                case Resource.Id.refresh:
                    if (!refreshLayout.Refreshing) {
                        refreshLayout.Refreshing = true;
                        manager.StartRefresh();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return base.OnOptionsItemSelected(item);
        }

        public override void OnResume() {
            base.OnResume();
            refreshLayout = (SwipeRefreshLayout)View.FindViewById(Resource.Id.refreshLayout_overview);
            manager = new OverviewManager(this, act.GetAppManager());
            ShowEvents();
            refreshLayout.Refresh += (sender, e) => {
                manager.StartRefresh();
            };
        }

        public void ShowEvents() {
            if (View != null) {
                ListView overviewList = (ListView)View.FindViewById(Resource.Id.listView_overview);
                List<Event> events = act.GetAppManager().GetEvents();
                if (events.Count > 0) {
                    overviewList.Visibility = ViewStates.Visible;
                    OverviewAdapter adapter = new OverviewAdapter(events, act, act.GetAppManager());
                    overviewList.Adapter = adapter;
                    Event selected = act.GetAppManager().GetSelected();
                    int index = events.IndexOf(selected);
                    overviewList.SetSelection(Math.Max(index, 0));
                }
                else {
                    overviewList.Visibility = ViewStates.Invisible;
                }
                overviewList.Invalidate();
            }
        }

        public void StopRefreshing() {
            refreshLayout.Refreshing = false;
        }

        public override void Update() {
        }
    }
}