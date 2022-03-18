using SwissO.Parser;
using System;
using System.Collections.Generic;
using static SwissO.Event;
using static SwissO.MyResources;

namespace SwissO {

    interface OverviewPage {
        public void ShowEvents();

        public void StopRefreshing();
    }

    class OverviewManager : PageManager {

        private OverviewPage page;

        private List<Parser.Parser.RequestCodes> runningParser = new List<Parser.Parser.RequestCodes>();

        public OverviewManager(OverviewPage page, AppManager appManager) : base(appManager) {
            this.page = page;
            if(appManager.GetEvents().Count == 0) {
                StartRefresh();
            }
            page.ShowEvents();
        }

        public void StartRefresh() {
            SOLVParser solvParser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVEventlist);
            runningParser.Add(Parser.Parser.RequestCodes.SOLVEventlist);
            solvParser.StartEventlistRequest();
            PicoParser picoParser = new PicoParser(httpClient, this, Parser.Parser.RequestCodes.PicoEventlist);
            runningParser.Add(Parser.Parser.RequestCodes.PicoEventlist);
            picoParser.StartEventlistRequest();
        }

        public void FinishedEventlistLoading(Parser.Parser.RequestCodes requestCode) {
            runningParser.Remove(requestCode);
            if(runningParser.Count == 0) {
                page.StopRefreshing();
                appManager.InitEvents(); //Quick way to sort
                page.ShowEvents();
            }
        }

        public void Add(Event newEvent) {
            List<Event> events = appManager.GetEvents();
            foreach (Event e in events) {
                if (e.Equals(newEvent)) {
                    e.Merge(newEvent);
                    appManager.GetDaten().UpdateEvent(e);
                    return;
                }
            }
            int id = appManager.GetDaten().InsertEvent(newEvent);
            newEvent.SetId(id);
            events.Add(newEvent);
        }

        public static (UriArt[], StringResource[]) GetUrisSorted(Event e) {
            bool over = DateTime.Compare(e.Date, DateTime.Today) < 0 || e.Rangliste != null;
            bool deadlinePassed = DateTime.Compare(e.Deadline, DateTime.Today) < 0;
            UriArt[] uris;
            StringResource[] resources;
            if (over) {
                uris = new UriArt[] { UriArt.Ausschreibung, UriArt.Weisungen, UriArt.Startliste, UriArt.Rangliste, UriArt.WKZ };
                resources = new StringResource[] { StringResource.Ausschreibung, StringResource.Weisungen, StringResource.Startlist, StringResource.Rangliste,
                StringResource.Wkz};
            }
            else if (deadlinePassed) {
                uris = new UriArt[] { UriArt.Ausschreibung, UriArt.Weisungen, UriArt.Startliste, UriArt.Liveresultate, UriArt.WKZ, UriArt.Mutation };
                resources = new StringResource[] { StringResource.Ausschreibung, StringResource.Weisungen, StringResource.Startlist, StringResource.Liveresult,
                StringResource.Wkz, StringResource.Mutation};
            }
            else {
                uris = new UriArt[] { UriArt.Ausschreibung, UriArt.Weisungen, UriArt.Anmeldung, UriArt.Startliste, UriArt.Liveresultate, UriArt.WKZ, UriArt.Mutation };
                resources = new StringResource[] { StringResource.Ausschreibung, StringResource.Weisungen, StringResource.Anmeldung, StringResource.Startlist,
                StringResource.Liveresult, StringResource.Wkz, StringResource.Mutation};
            }

            return (uris, resources);
        }
    }
}
