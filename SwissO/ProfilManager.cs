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

        private Daten daten;
        private Profil profil;
        private IProfilPage page;

        public ProfilManager(Daten daten, IProfilPage page) {
            this.daten = daten;
            this.page = page;
        }


        public (string vorname, string nachname, int si, string cat) LoadData() {
            profil = daten.CreateProfil();
            return profil.Get();
        }

        public void SaveData(string vorname, string nachname, int siCard, string category) {
            profil.Update(vorname, nachname, siCard, category);
            profil.Save(daten);
        }

        public int GetProfilID() {
            return profil.GetID();
        }

        public void NamenCallback(bool club, string name) {
            if (club) {
                daten.InsertClub(name, profil.GetID());
            }
            else {
                daten.InsertFreund(name, profil.GetID());
            }

            page.ShowFriendsAndClub();
        }
    }
}
