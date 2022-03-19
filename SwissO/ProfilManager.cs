using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO {

    public interface IProfilPage {

        /*public enum EditText { Name, SICard, Category }
        void SetText(EditText editText, string text);
        string GetText(EditText editText);*/
        void ShowFriendsAndClub();
    }

    public class ProfilManager {

        private Profil profil;
        private IProfilPage page;

        private AppManager appManager;

        public ProfilManager(AppManager app, IProfilPage page) {
            this.page = page;
            appManager = app;
        }


        public (string vorname, string nachname, int si, string cat) LoadData() {
            profil = appManager.GetDaten().CreateProfil();
            return profil.Get();
        }

        public void SaveData(string vorname, string nachname, int siCard, string category) {
            profil.Update(vorname, nachname, siCard, category);
            profil.Save(appManager.GetDaten());
        }

        public int GetProfilID() {
            return profil.GetID();
        }

        public void NamenCallback(bool club, string name) {
            if (club) {
                appManager.GetDaten().InsertClub(name, profil.GetID());
            }
            else {
                appManager.GetDaten().InsertFreund(name, profil.GetID());
            }

            page.ShowFriendsAndClub();
        }
    }
}
