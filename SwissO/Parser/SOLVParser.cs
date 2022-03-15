using HtmlAgilityPack;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;

namespace SwissO.Parser {

    internal class ProvEvent {
        public int id;
        public string[] titles;
        public string[] fields;

        public ProvEvent(int id, string[] titles, string[] fields) {
            this.id = id;
            this.titles = titles;
            this.fields = fields;
        }
    }

    public class SOLVParser : Parser {

        private const string COLUMN_SOLVId = "unique_id";
        private const string COLUMN_Date = "date";
        private const string COLUMN_Region = "region";
        private const string COLUMN_Title = "event_name";
        private const string COLUMN_LAusschreibung = "event_link";
        private const string COLUMN_Club = "club";
        private const string COLUMN_Map = "map";
        private const string COLUMN_SKoordN = "coord_y";
        private const string COLUMN_SKoordE = "coord_x";
        private const string COLUMN_Deadline = "deadline";
        private const string COLUMN_EntryPortal = "entryportal";

        private const string RANGLISTE = "Rangliste";
        private const string STARTLISTE = "Startliste";
        private const string LIVERESULTATE = "Live Results";

        //private const string COLUMN_LWeisungen = "weisungen";
        //private const string COLUMN_LRangliste = "rangliste";
        //private const string COLUMN_LLiveRangliste = "liverangliste";
        //private const string COLUMN_LStartliste = "startliste";

        private List<ProvEvent> provEvents = new List<ProvEvent>();
        private List<int> idsUnfinished = new List<int>();

        public SOLVParser(MyHttpClient client, PageManager manager, RequestCodes requestCode) : base(client, manager, requestCode) {
        }

        public void StartEventlistRequest() {
            provEvents.Clear();
            httpClient.SendStringRequest(this, "https://www.o-l.ch/cgi-bin/fixtures?&year=2022&kind=-1&csv=1", MyHttpClient.RequestCodes.Eventliste, 0);
        }

        private void LoadEventList(string csv) {
            idsUnfinished.Clear();
            string[] rows = csv.Split('\n');
            string[] titles = rows[0].Split(';');
            for (int i = 1; i < rows.Length - 1; i++) {
                string[] fields = rows[i].Split(';');
                int solvid = GetInt(fields[Array.IndexOf(titles, COLUMN_SOLVId)]);
                string club = fields[Array.IndexOf(titles, COLUMN_Club)].Trim();
                if (club.Trim() != "Swiss Orienteering") {
                    provEvents.Add(new ProvEvent(solvid, titles, fields));
                    idsUnfinished.Add(solvid);
                    httpClient.SendStringRequest(this, "https://www.o-l.ch/cgi-bin/fixtures?mode=show&unique_id=" + solvid, MyHttpClient.RequestCodes.SingleEvent, solvid);
                }
            }
        }

        private void LoadSingleEvent(int id, string html) {
            idsUnfinished.Remove(id);
            ProvEvent e = null;
            foreach (ProvEvent i in provEvents) {
                if (i.id == id) {
                    e = i;
                    break;
                }
            }

            string title = e.fields[Array.IndexOf(e.titles, COLUMN_Title)].Trim();
            int solvid = GetInt(e.fields[Array.IndexOf(e.titles, COLUMN_SOLVId)]);
            DateTime date = Helper.GetDate(e.fields[Array.IndexOf(e.titles, COLUMN_Date)]);
            string region = e.fields[Array.IndexOf(e.titles, COLUMN_Region)].Trim();
            string map = e.fields[Array.IndexOf(e.titles, COLUMN_Map)].Trim();
            DateTime deadline = Helper.GetDate(e.fields[Array.IndexOf(e.titles, COLUMN_Deadline)]);
            int skoordn = GetInt(e.fields[Array.IndexOf(e.titles, COLUMN_SKoordN)]);
            int skoorde = GetInt(e.fields[Array.IndexOf(e.titles, COLUMN_SKoordE)]);
            (double intn, double inte) = Helper.CalcSwiss(skoordn, skoorde);
            string lausschreibung = e.fields[Array.IndexOf(e.titles, COLUMN_LAusschreibung)];
            string club = e.fields[Array.IndexOf(e.titles, COLUMN_Club)].Trim();
            int portal = GetInt(e.fields[Array.IndexOf(e.titles, COLUMN_EntryPortal)]);

            HtmlDocument document = new HtmlDocument();
            document.LoadHtml(html);
            HtmlNodeCollection tr = document.DocumentNode.SelectNodes("//html/body/table[2]/tr/td[2]/table/tr/td[1]/table/tr");
            string rangliste = "";
            string startliste = "";
            string liveresultate = "";
            foreach (HtmlNode node in tr) {
                if (node.InnerText.Contains(RANGLISTE)) {
                    rangliste = "https://www.o-l.ch/cgi-bin/" + node.FirstChild.FirstChild.Attributes["href"].Value;
                }
                if (node.InnerText.Contains(STARTLISTE)) {
                    startliste = "https://www.o-l.ch/cgi-bin/" + node.FirstChild.FirstChild.Attributes["href"].Value;
                }
                if (node.InnerText.Contains(LIVERESULTATE)) {
                    liveresultate = node.FirstChild.FirstChild.Attributes["href"].Value;
                }
            }
            if (manager is OverviewManager ovmanager) {
                ovmanager.Add(new Event(title, date, club, map, region, intn, inte, deadline, lausschreibung, null, null, null, startliste, liveresultate, rangliste, portal));
            }
            if (idsUnfinished.Count == 0) {
                manager.OnFinished(requestCode);
            }

        }


        private static int GetInt(string s) {
            if (string.IsNullOrWhiteSpace(s)) {
                return Helper.intnull;
            }
            return int.Parse(s);
        }

        public void StartRanglisteRequest(Event e) {
            httpClient.SendStringRequest(this, e.Rangliste.ToString() + "&kind=all", MyHttpClient.RequestCodes.Rangliste, e.Id);
        }

        private void LoadRangliste(string html) {
            if (manager is ListManager rgmanager) {
                Event e = rgmanager.GetAppManager().GetSelected();
                List<Laeufer> laeufer = new List<Laeufer>();
                HtmlDocument htmlDoc = new HtmlDocument();
                htmlDoc.LoadHtml(html);
                HtmlNode body = htmlDoc.DocumentNode.SelectSingleNode("//html/body/table[2]/tr/td[2]");
                HtmlNodeCollection kategories = body.SelectNodes("./b");
                //HtmlNodeCollection data = body.SelectNodes("./pre");
                for (int i = 0; i < kategories.Count; i++) {
                    HtmlNode data = NextNodeWithName(kategories[i], "pre");
                    if (data != null) {
                        string kat = kategories[i].InnerText.Trim();
                        string resultate = data.InnerText;
                        string[] rows = resultate.Split("\n");
                        for (int j = 2; j < rows.Length - 1; j++) {
                            string akt = rows[j];
                            string rang = akt.Substring(0, 3).Trim();
                            string name = akt.Substring(5, 23).Trim();
                            string vorname = name.Split(" ")[0];
                            string nachname = name.Split(" ")[1];
                            string jahrgang = akt.Substring(28, 2).Trim();
                            string ort = akt.Substring(32, 19).Trim();
                            string club = akt.Substring(51, 19).Trim();
                            string zeit = akt.Substring(70, 8).Trim();
                            laeufer.Add(new Laeufer(e, vorname, nachname, jahrgang, club, kat, null, null, rang, zeit));
                        }
                    }
                }
                rgmanager.LoadList(laeufer);
            }
        }

        private static HtmlNode NextNodeWithName(HtmlNode node, string name) {
            while(node != null) {
                if(node.Name == name) {
                    return node;
                }
                node = node.NextSibling;
            }
            return null;
        }

        public override void onResult(MyHttpClient.RequestCodes requestCode, int id, string html) {
            switch (requestCode) {
                case MyHttpClient.RequestCodes.Rangliste:
                    LoadRangliste(html);
                    break;
                case MyHttpClient.RequestCodes.Eventliste:
                    LoadEventList(html);
                    break;
                case MyHttpClient.RequestCodes.SingleEvent:
                    LoadSingleEvent(id, html);
                    break;
            }
        }
    }
}
