using System;
using System.Collections.Generic;

namespace SwissO {

    public class Profil {

        private readonly int id;
        private string vorname;
        private string nachname;
        private int siCard;
        private string category;
        private List<string> clubs = new List<string>();
        private List<string> freunde = new List<string>();

        public Profil(MyCursor cursor, Daten daten) {
            id = cursor.GetInt(0);
            vorname = cursor.GetString(1);
            nachname = cursor.GetString(2);
            siCard = cursor.GetInt(3);
            category = cursor.GetString(4);
            cursor.Close();
            cursor = daten.GetFreundeByProfil(id);
            while (cursor.Read()) {
                freunde.Add(cursor.GetString(1));
            }
            cursor.Close();
            cursor = daten.GetClubsByProfil(id);
            while (cursor.Read()) {
                clubs.Add(cursor.GetString(1));
            }
            cursor.Close();
        }

        public void Update(string vorname, string nachname, int siCard, string category) {
            if(vorname != null) {
                this.vorname = vorname;
            }
            if(nachname != null) {
                this.nachname = nachname;
            }
            if (siCard != Helper.intnull) {
                this.siCard = siCard;
            }
            if (category != null) {
                this.category = category;
            }
        }

        public int GetID() {
            return id;
        }

        public (string vorname, string nachname, int si, string cat) Get() {
            return (vorname, nachname, siCard, category);
        }

        public void Save(Daten daten) {
            daten.UpdateProfil(id, vorname, nachname, siCard, category);
        }

        public List<string> GetClubs() {
            return clubs;
        }
        
        public List<string> GetFriends() {
            return freunde;
        }
    }
}
