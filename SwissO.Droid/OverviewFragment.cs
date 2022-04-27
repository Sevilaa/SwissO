using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using AndroidX.Fragment.App;
using AndroidX.SwipeRefreshLayout.Widget;
using System;
using System.Collections.Generic;

namespace SwissO.Droid {

    public class OverviewFragment : Fragment, OverviewPage {

        private OverviewManager manager;

        private SwipeRefreshLayout refreshLayout;
        private EditText schnellfilter;

        private MainActivity act;

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
                        manager.StartRefresh();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return base.OnOptionsItemSelected(item);
        }

        public override void OnViewCreated(View view, Bundle savedInstanceState) {
            base.OnViewCreated(view, savedInstanceState);
            act = (MainActivity)Activity;
            act.SetTitle(Resource.String.overview);
            refreshLayout = (SwipeRefreshLayout)View.FindViewById(Resource.Id.refreshLayout_overview);
            schnellfilter = (EditText)View.FindViewById(Resource.Id.schnellfilter);
            manager = new OverviewManager(this, act.GetAppManager());
            refreshLayout.Refresh += (sender, e) => {
                manager.StartRefresh();
            };
            schnellfilter.TextChanged += (sender, e) => {
                ShowEvents();
            };
        }

        public void ShowEvents() {
            if (View != null) {
                string filter = schnellfilter.Text;
                ListView overviewList = (ListView)View.FindViewById(Resource.Id.listView_overview);
                List<Event> events = act.GetAppManager().GetFilteredEvents(filter);
                if (events.Count > 0) {
                    overviewList.Visibility = ViewStates.Visible;
                    OverviewAdapter adapter = new OverviewAdapter(events, act, act.GetAppManager());
                    overviewList.Adapter = adapter;
                    Event selected = act.GetAppManager().GetSelected();
                    if (!events.Contains(selected)) {
                        selected = AppManager.GetUpComingEvent(events);
                    }
                    int index = events.IndexOf(selected);
                    overviewList.SetSelection(Math.Max(index, 0));
                }
                else {
                    overviewList.Visibility = ViewStates.Invisible;
                }
                overviewList.Invalidate();
            }
        }

        public void SetRefreshing(bool b) {
            refreshLayout.Refreshing = b;
        }
    }
}