using SwissO.Parser;
using System;
using System.Collections.Generic;

namespace SwissO {

    public interface IListPage {
        void UpdateList();

        void ShowNotAvailable();
        void ShowProgressBar();
        void ShowList();
        void ShowOnlyInWebBrowser();
        bool GetBoolPref(string key, bool def);
        string GetStringPref(string key, string def);

    }

    public class ListManager : PageManager {

        public enum ListType { Startliste, Rangliste }

        private readonly ListType listType;

        private readonly IListPage page;
        private readonly Profil profil;

        public ListManager(IListPage page, AppManager appManager, ListType listType) : base(appManager) {
            this.page = page;
            this.listType = listType;
            profil = appManager.GetDaten().CreateProfil();

        }

        public void InitEvent() {
            if (listType == ListType.Startliste) {
                SendStartlisteRequest();
            }
            else {
                SendRanglisteRequest();
            }

        }

        public void SendStartlisteRequest() {
            Event selected = appManager.GetSelected();
            if (selected != null && selected.Startliste != null) {
                page.ShowProgressBar();
                if (selected.Startliste.OriginalString.Contains("entry.picoevents.ch")) {
                    PicoParser picoParser = new PicoParser(httpClient, this, Parser.Parser.RequestCodes.PicoStartliste);
                    picoParser.StartStartlisteRequest(selected);
                }
                else if (selected.Startliste.OriginalString.Contains("o-l.ch/cgi-bin/results?type=start&")) {
                    SOLVParser solvParser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVStartliste);
                    solvParser.StartStartlisteRequest(selected);
                }
                else {
                    page.ShowOnlyInWebBrowser();
                }
            }
            else {
                page.ShowNotAvailable();
            }
        }

        public void SendRanglisteRequest() {
            Event selected = appManager.GetSelected();
            if (selected != null && selected.Rangliste != null) {
                SOLVParser parser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVRangliste);
                parser.StartRanglisteRequest(selected);
                page.ShowProgressBar();
            }
            else {
                page.ShowNotAvailable();
            }
        }

        public List<Laeufer> GetAlleLaeufer(string filter) {
            (string order, string orderColumn) = OrderString();
            MyCursor cursor = appManager.GetDaten().GetAllLaeuferByEvent(appManager.GetSelected(), filter, order);
            return CreateLaeufer(cursor, orderColumn);
        }

        public List<Laeufer> GetClubLaeufer(string filter) {
            (string order, string orderColumn) = OrderString();
            MyCursor cursor = appManager.GetDaten().GetClubLaeuferByEvent(appManager.GetSelected(), profil.GetClubs(), filter, order);
            return CreateLaeufer(cursor, orderColumn);
        }

        public List<Laeufer> GetFriendsLaeufer(string filter) {
            (string order, string orderColumn) = OrderString();
            MyCursor cursor = appManager.GetDaten().GetFriendLaeuferByEvent(appManager.GetSelected(), profil.GetFriends(), filter, order);
            return CreateLaeufer(cursor, orderColumn);
        }

        private List<Laeufer> CreateLaeufer(MyCursor cursor, string orderColumn) {
            List<Laeufer> laeuferList = new List<Laeufer>();
            List<Laeufer> nullList = new List<Laeufer>();
            while (cursor.Read()) {
                if (orderColumn == null) {
                    laeuferList.Add(new Laeufer(cursor));
                }
                else if (cursor.IsNull(orderColumn)) {
                    nullList.Add(new Laeufer(cursor));
                }
                else {
                    laeuferList.Add(new Laeufer(cursor));
                }
            }
            laeuferList.AddRange(nullList);
            return laeuferList;
        }

        private (string, string) OrderString() {
            string column;
            bool ascending;
            if (listType == ListType.Startliste) {
                column = page.GetStringPref(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
                ascending = page.GetBoolPref(Helper.Keys.sorting_startlist_ascending, Helper.Defaults.sorting_startlist_ascending);
            }
            else {
                column = page.GetStringPref(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
                ascending = page.GetBoolPref(Helper.Keys.sorting_ranglist_ascending, Helper.Defaults.sorting_ranglist_ascending);
            }
            if (column == Helper.original) {
                return (null, null);
            }
            string order = column + (ascending ? " ASC;" : " DESC;");
            return (order, column);
        }

        public void LoadList() {
            int count = appManager.GetDaten().GetLaeuferCountByEvent(appManager.GetSelected());
            if (count > 0) {
                page.UpdateList();
            }
            else if (appManager.GetSelected().Startliste == null) {
                page.ShowNotAvailable();
            }
            else {
                page.ShowOnlyInWebBrowser();
            }
        }

        public ListType GetListType() {
            return listType;
        }
    }
}
