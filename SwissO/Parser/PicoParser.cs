using HtmlAgilityPack;
using System;
using System.Collections.Generic;
using System.Globalization;

namespace SwissO.Parser {
    class PicoParser : Parser {

        public PicoParser(MyHttpClient client, PageManager manager, RequestCodes requestCode) : base(client, manager, requestCode) {
        }

        public void StartStartlisteRequest(Event e) {
            httpClient.SendStringRequest(this, e.Startliste.OriginalString, MyHttpClient.RequestCodes.Startliste, 0);
        }

        public void LoadStartliste(string html) {
            if (manager is ListManager slmanager && slmanager.GetListType() == ListManager.ListType.Startliste) {
                Event e = slmanager.GetAppManager().GetSelected();
                Daten daten = slmanager.GetAppManager().GetDaten();
                daten.DeleteAllLaeuferByEvent(e);
                HtmlDocument htmlDoc = new HtmlDocument();
                htmlDoc.LoadHtml(html);
                HtmlNode body = htmlDoc.DocumentNode.SelectSingleNode("//html/body/div/font/div/p/body");
                if(body == null) {
                    body = htmlDoc.DocumentNode.SelectSingleNode("//html/body/div/font/div/body");
                }
                while (body != null) {
                    HtmlNode strong = body.SelectSingleNode("./p/strong");
                    string cat = strong.InnerText;
                    HtmlNodeCollection trs = body.SelectNodes("./table/tr");
                    foreach (HtmlNode tr in trs) {
                        HtmlNodeCollection td = tr.SelectNodes("./td");
                        string startZeit = td[5].InnerText;
                        TimeSpan start = TimeSpan.MinValue;
                        if(startZeit != "bezahlt" && !string.IsNullOrWhiteSpace(startZeit)) {
                            CultureInfo culture = new CultureInfo("de-CH");
                            bool success = TimeSpan.TryParseExact(startZeit, @"h\:mm", new CultureInfo("de-CH"), TimeSpanStyles.None, out start);
                            if(!success) {
                                start = TimeSpan.MinValue;
                            }

                        }
                        daten.InsertLaeufer(td[1].InnerText + " " + td[0].InnerText, Helper.intnull, td[4].InnerText, cat, Helper.intnull, start , TimeSpan.MinValue, Helper.intnull, e);
                    }
                    body = body.SelectSingleNode("./body");
                }
                slmanager.LoadList();
            }
        }

        public void StartEventlistRequest() {
            if (manager is OverviewManager) {
                httpClient.SendStringRequest(this, "https://entry.picoevents.ch/", MyHttpClient.RequestCodes.Eventliste, 0);
            }
        }

        private void LoadEventlist(string html) {
            HtmlDocument htmlDoc = new HtmlDocument();
            htmlDoc.LoadHtml(html);
            HtmlNodeCollection trs = htmlDoc.DocumentNode.SelectNodes("//html/body/div/h2[1]/table/tr");
            for (int i = 2; i < trs.Count; i++) {
                HtmlNodeCollection tds = trs[i].SelectNodes("./td");
                string title = tds[0].InnerText.Trim();
                DateTime date = Helper.GetDate(tds[1].InnerText);
                string lausschreibung = GetLink(tds[2]);
                string lanmeldung = GetLink(tds[3]);
                string lstartliste = GetLink(tds[4]);
                string lmutation = GetLink(tds[5]);
                DateTime deadline = Helper.GetDate(tds[6].InnerText);
                ((OverviewManager)manager).Add(new Event(title, date, null, null, null, Helper.intnull, Helper.intnull, deadline, lausschreibung, null, lanmeldung, lmutation, lstartliste, null, null, 2));
            }
            ((OverviewManager)manager).FinishedEventlistLoading(requestCode);
        }

        private void StartLiveResultateRequest(Event e) {
            httpClient.SendStringRequest(this, "https://n.ethz.ch/~laaschs/ALL.htm", MyHttpClient.RequestCodes.Rangliste, e.Id);
        }

        private void LoadLiveResultate(string html) {
            if (manager is ListManager rgmanager && rgmanager.GetListType() == ListManager.ListType.Rangliste) {
                //List<Laeufer> rangliste = new List<Laeufer>();
                HtmlDocument htmlDoc = new HtmlDocument();
                htmlDoc.LoadHtml(html);
                HtmlNodeCollection trs = htmlDoc.DocumentNode.SelectNodes("/html/body/div/table/tr");

            }
        }

        private static string GetLink(HtmlNode td) {
            HtmlNodeCollection children = td.ChildNodes;
            if (children.Count > 1) {
                return "https://entry.picoevents.ch/" + td.ChildNodes[1].Attributes["href"].Value.TrimEnd();
            }
            return null;
        }

        public override void onResult(MyHttpClient.RequestCodes requestCode, int id, string html) {
            switch (requestCode) {
                case MyHttpClient.RequestCodes.Eventliste:
                    LoadEventlist(html);
                    break;
                case MyHttpClient.RequestCodes.Startliste:
                    LoadStartliste(html);
                    break;
                case MyHttpClient.RequestCodes.Rangliste:
                    LoadLiveResultate(html);
                    break;
                default:
                    throw new NotImplementedException();
            }
        }
    }
}
