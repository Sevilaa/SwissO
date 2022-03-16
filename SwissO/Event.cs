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
            Ausschreibung = newUri(ausschreibung);
            Weisungen = newUri(weisungen);
            Anmeldung = newUri(anmeldung);
            Mutation = newUri(mutation);
            Startliste = newUri(startliste);
            Liveresultate = newUri(liveresultate);
            Rangliste = newUri(rangliste);
            Eventportal = portal;
        }

        public Event(MyCursor cursor) {
            Id = cursor.GetInt(0);
            Title = cursor.GetString(1);
            Date = cursor.GetDate(2);
            Region = cursor.GetString(3);
            Club = cursor.GetString(4);
            Map = cursor.GetString(5);
            Deadline = cursor.GetDate(6);
            Koordn = cursor.GetDouble(7);
            Koorde = cursor.GetDouble(8);
            Ausschreibung = cursor.GetUri(9);
            Weisungen = cursor.GetUri(10);
            Rangliste = cursor.GetUri(11);
            Liveresultate = cursor.GetUri(12);
            Startliste = cursor.GetUri(13);
            Anmeldung = cursor.GetUri(14);
            Mutation = cursor.GetUri(15);
            Eventportal = cursor.GetInt(16);
        }

        public void SetId(int id) {
            this.Id = id;
        }

        private static Uri newUri(string s) {
            Uri.TryCreate(s, UriKind.RelativeOrAbsolute, out Uri uri);
            return uri;
        }

        public bool equals(Event e) {
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