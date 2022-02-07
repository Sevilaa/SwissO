using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO {
    public class Laeufer {
        public Laeufer(Event e, string vorname, string nachname, string jahrgang, string club, string category, string startnummer, string starttime, string rang, string zielzeit) {
            this.category = category;
            this.vorname = vorname;
            this.nachname = nachname;
            this.starttime = starttime;
            this.club = club;
            this.zielzeit = zielzeit;
            eventt = e;
            this.jahrgang = jahrgang;
            this.startnummer = startnummer;
            this.rang = rang;

        }

        public Event eventt { get; }
        public string vorname { get; }
        public string nachname { get; }
        public string jahrgang { get; }
        public string club { get; }
        public string category { get; }
        public string startnummer { get; }
        public string starttime { get; }
        public string rang { get; }
        public string zielzeit { get; }


        public string StartzeitString() {
            return "(" + starttime + " - " + category + ": " + vorname + " " + nachname + ")";
        }

        public string ZielzeitString() {
            return "(" + rang + ". - " + category + ": " + vorname + " " + nachname + ": " + zielzeit + ")";
        }

        public bool CompareClub(string club) {
            if(string.IsNullOrWhiteSpace(this.club) || string.IsNullOrWhiteSpace(club)) {
                return false;
            }
            return club.Contains(this.club);
        }

        public bool CompareFriend(Friend friend) {
            return vorname.Contains(friend.vorname) && nachname.Contains(friend.nachname);
        }
    }
}
