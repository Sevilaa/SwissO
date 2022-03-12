using SwissO.Parser;
using System;
using System.Collections.Generic;
using System.Linq;

namespace SwissO {

    public interface App {

        void OpenRangliste();
        void OpenStartliste();
        void OpenWebBrowser(Uri uri);
    }
    public class AppManager {

        private App app;

        private Daten daten;

        private Event selected;

        private MyHttpClient httpClient;

        private List<Event> events = new List<Event>();

        public AppManager(App app, Daten daten, MyHttpClient client) {
            this.app = app;
            this.daten = daten;
            httpClient = client;
            InitEvents();
            selected = GetUpComingEvent();
            selected ??= GetLastEvent();
        }

        public void InitEvents() {
            MyCursor cursor = daten.GetAllEvents();
            events.Clear();
            while (cursor.Read()) {
                events.Add(new Event(cursor));
            }
        }

        public void OpenEventDetails(Event e, Event.UriArt uriArt) {
            selected = e;
            switch (uriArt) {
                case Event.UriArt.Rangliste:
                    app.OpenRangliste();
                    break;
                case Event.UriArt.Startliste:
                    app.OpenStartliste();
                    break;
                default:
                    app.OpenWebBrowser(e.GetUri(uriArt));
                    break;
            }
        }

        public void SetEvent(Event e) {
            selected = e;
        }

        public Event[] GetEventSelectionables() {
            List<Event> selectionables = new List<Event>();
            int middleIndex = events.IndexOf(selected);
            int beginIndex = Math.Max(middleIndex - Helper.selectionablesLength / 2, 0);
            for (int i = 0; i < Helper.selectionablesLength && beginIndex + i < events.Count; i++) {
                selectionables.Add(events[i + beginIndex]);
            }
            return selectionables.ToArray();
        }

        private Event GetUpComingEvent() {
            return events.Where(i => i.Date >= DateTime.Today).FirstOrDefault();
        }

        private Event GetLastEvent() {
            return events.Where(i => i.Date <= DateTime.Today).LastOrDefault();
        }

        public List<Event> GetEvents() {
            return events;
        }

        public Daten GetDaten() {
            return daten;
        }

        public MyHttpClient GetHttpClient() {
            return httpClient;
        }

        public Event GetSelected() {
            return selected;
        }
    }
}
