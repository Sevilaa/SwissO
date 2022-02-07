using SwissO.Parser;
using System;
using System.Collections.Generic;

namespace SwissO {

    public interface IListPage {
        void UpdateList();
    }

    public class ListManager : PageManager {

        public enum ListType { Startliste, Rangliste}

        private readonly ListType listType;

        private IListPage page;
        private Profil profil;

        private List<Laeufer> alleLaeufer;
        private List<Laeufer> friendsLaeufer;
        private List<Laeufer> clubLaeufer;

        public ListManager(IListPage page, AppManager appManager, ListType listType) : base(appManager) {
            this.page = page;
            this.listType = listType;
            profil = appManager.GetDaten().CreateProfil();
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
                if (selected.Startliste.OriginalString.Contains("entry.picoevents.ch")) {
                    PicoParser picoParser = new PicoParser(httpClient, this, Parser.Parser.RequestCodes.PicoStartliste);
                    picoParser.StartStartlisteRequest(selected);
                }
            }
        }

        public void SendRanglisteRequest() {
            Event selected = appManager.GetSelected();
            if (selected != null) {
                SOLVParser parser = new SOLVParser(httpClient, this, Parser.Parser.RequestCodes.SOLVRangliste);
                parser.StartRanglisteRequest(selected);
            }
        }

        public void LoadList(List<Laeufer> list) {
            alleLaeufer = list;
            clubLaeufer = new List<Laeufer>();
            friendsLaeufer = new List<Laeufer>();
            List<string> clubs = profil.GetClubs();
            List<Friend> freunde = profil.GetFriends();
            foreach (Laeufer laeufer in list) {
                foreach (string club in clubs) {
                    if (laeufer.CompareClub(club)) {
                        clubLaeufer.Add(laeufer);
                    }
                }
                foreach (Friend friend in freunde) {
                    if (laeufer.CompareFriend(friend)) {
                        friendsLaeufer.Add(laeufer);
                    }
                }
            }
            page.UpdateList();
        }

        public ListType GetListType() {
            return listType;
        }

        public List<Laeufer> GetAlleLaeufer() {
            return alleLaeufer;
        }
        
        public List<Laeufer> GetClubLaeufer() {
            return clubLaeufer;
        }
        
        public List<Laeufer> GetFriendsLaeufer() {
            return friendsLaeufer;
        }

        public override void OnFinished(Parser.Parser.RequestCodes requestCode) {
            throw new NotImplementedException();
        }
    }
}
