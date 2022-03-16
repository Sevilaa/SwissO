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

        public enum ListType { Startliste, Rangliste}

        private readonly ListType listType;

        private IListPage page;
        private Profil profil;

        public ListManager(IListPage page, AppManager appManager, ListType listType) : base(appManager) {
            this.page = page;
            this.listType = listType;
            profil = appManager.GetDaten().CreateProfil();
            InitEvent();
            
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
            if(selected != null && selected.Startliste != null) {
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

        public MyCursor GetAlleLaeufer(string filter) {
            return appManager.GetDaten().GetAllLaeuferByEvent(appManager.GetSelected(), filter, OrderString());
        }

        public MyCursor GetClubLaeufer(string filter) {
            return appManager.GetDaten().GetClubLaeuferByEvent(appManager.GetSelected(), profil.GetClubs(), filter, OrderString());
        }

        public MyCursor GetFriendsLaeufer(string filter) {
            return appManager.GetDaten().GetFriendLaeuferByEvent(appManager.GetSelected(), profil.GetFriends(), filter, OrderString());
        }

        private string OrderString() {
            if(listType == ListType.Startliste) {
                return page.GetStringPref(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column) +
                (page.GetBoolPref(Helper.Keys.sorting_startlist_ascending, Helper.Defaults.sorting_startlist_ascending) ? " ASC;" : " DESC;");
            }
            else {
                return page.GetStringPref(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column) +
                (page.GetBoolPref(Helper.Keys.sorting_ranglist_ascending, Helper.Defaults.sorting_ranglist_ascending) ? " ASC;" : " DESC;");
            }
        }

        public void LoadList() {
            int count = appManager.GetDaten().GetLaeuferCountByEvent(appManager.GetSelected());
            if (count > 0) {
                page.UpdateList();
            }
            else {
                page.ShowNotAvailable();
            }
        }

        public ListType GetListType() {
            return listType;
        }
    }
}
