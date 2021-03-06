using System;
using System.Collections.Generic;

namespace SwissO {
    public class Event {

        public enum UriArt { Ausschreibung, Weisungen, Anmeldung, Mutation, Startliste, Rangliste, WKZ, Liveresultate}
        public int Id { get; private set; }
        public string Title { get; private set; }
        public DateTime Date { get; private set; }
        public string Club { get; private set; }
        public string Map { get; private set; }
        public string Region { get; private set; }
        public double Koordn { get; private set; }
        public double Koorde { get; private set; }
        public DateTime Deadline { get; private set; }
        public Uri Ausschreibung { get; private set; }
        public Uri Weisungen { get; private set; }
        public Uri Anmeldung { get; private set; }
        public Uri Mutation { get; private set; }
        public Uri Startliste { get; private set; }
        public Uri Liveresultate { get; private set; }
        public Uri Rangliste { get; private set; }
        public int Eventportal { get; private set; }

        public Event(string title, DateTime date, string club, string map, string region, double koordn, double koorde, DateTime deadline,
            string ausschreibung, string weisungen, string anmeldung, string mutation, string startliste, string liveresultate, string rangliste, int portal) {
            Title = title;
            Date = date;
            Club = club;
            Map = map;
            Region = region;
            Koorde = koorde;
            Koordn = koordn;
            Deadline = deadline;
            Ausschreibung = NewUri(ausschreibung);
            Weisungen = NewUri(weisungen);
            Anmeldung = NewUri(anmeldung);
            Mutation = NewUri(mutation);
            Startliste = NewUri(startliste);
            Liveresultate = NewUri(liveresultate);
            Rangliste = NewUri(rangliste);
            Eventportal = portal;
        }

        public Event(MyCursor cursor) {
            Id = cursor.GetInt(SQLiteHelper.COLUMN_ID);
            Title = cursor.GetString(SQLiteHelper.COLUMN_Title);
            Date = cursor.GetDate(SQLiteHelper.COLUMN_Date);
            Region = cursor.GetString(SQLiteHelper.COLUMN_Region);
            Club = cursor.GetString(SQLiteHelper.COLUMN_Club);
            Map = cursor.GetString(SQLiteHelper.COLUMN_Map);
            Deadline = cursor.GetDate(SQLiteHelper.COLUMN_Deadline);
            Koordn = cursor.GetDouble(SQLiteHelper.COLUMN_IntKoordN);
            Koorde = cursor.GetDouble(SQLiteHelper.COLUMN_IntKoordE);
            Ausschreibung = cursor.GetUri(SQLiteHelper.COLUMN_LAusschreibung);
            Weisungen = cursor.GetUri(SQLiteHelper.COLUMN_LWeisungen);
            Rangliste = cursor.GetUri(SQLiteHelper.COLUMN_LRangliste);
            Liveresultate = cursor.GetUri(SQLiteHelper.COLUMN_LLiveRangliste);
            Startliste = cursor.GetUri(SQLiteHelper.COLUMN_LStartliste);
            Anmeldung = cursor.GetUri(SQLiteHelper.COLUMN_LAnmeldung);
            Mutation = cursor.GetUri(SQLiteHelper.COLUMN_LMutation);
            Eventportal = cursor.GetInt(SQLiteHelper.COLUMN_EntryPortal);
        }

        public void SetId(int id) {
            this.Id = id;
        }

        private static Uri NewUri(string s) {
            if (string.IsNullOrWhiteSpace(s)) {
                return null;
            }
            try {
                return new Uri(s);
            }
            catch {
                return null;
            }
        }

        public bool Equals(Event e) {
            bool b = e.Date.Ticks == Date.Ticks;
            b = b && e.Eventportal == Eventportal;
            if(Club != null && e.Club != null) {
                b = b && Club == e.Club;
            }
            if(Title.Length >= 5) {
                b = b && ((Title[0] == e.Title[0] && Title[1] == e.Title[1]) || (Title[0] == e.Title[4] && Title[1] == e.Title[5]) || (Title[4] == e.Title[0] && Title[5] == e.Title[1]));
            }
            else {
                b = b && (Title[0] == e.Title[0] && Title[1] == e.Title[1]);
            }
            return b;
        }

        public void Merge(Event e) {
            Region ??= e.Region;
            Club ??= e.Club;
            Map ??= e.Map;
            Deadline = Deadline == DateTime.MinValue ? e.Deadline : Deadline;
            Koorde = Koorde == Helper.intnull ? e.Koorde : Koorde;
            Koordn = Koordn == Helper.intnull ? e.Koordn : Koordn;
            Ausschreibung ??= e.Ausschreibung;
            Weisungen ??= e.Weisungen;
            Rangliste ??= e.Rangliste;
            Liveresultate ??= e.Liveresultate;
            Startliste ??= e.Startliste;
            Anmeldung ??= e.Anmeldung;
            Mutation ??= e.Mutation;
        }

        public Uri GetUri(UriArt uriArt) {
            switch (uriArt) {
                case UriArt.Ausschreibung:
                    return Ausschreibung;
                case UriArt.Weisungen:
                    return Weisungen;
                case UriArt.Anmeldung:
                    return Anmeldung;
                case UriArt.Mutation:
                    return Mutation;
                case UriArt.Startliste:
                    return Startliste;
                case UriArt.Rangliste:
                    return Rangliste;
                case UriArt.WKZ:
                    if (Koordn != Helper.intnull && Koorde != Helper.intnull) {
                        return new Uri("geo:" + Koordn + "," + Koorde + "?q=" + Koordn + "," + Koorde + "(WKZ)");
                    }
                    return null;
                case UriArt.Liveresultate:
                    return Liveresultate;
                default:
                    throw new NotImplementedException();
            }
        }
    }
}