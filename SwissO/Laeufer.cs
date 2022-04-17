using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO {
    public class Laeufer {
        //public Laeufer(Event e, string name, string jahrgang, string club, string category, string startnummer, string starttime, string rang, string zielzeit) {
        //    this.category = category;
        //    this.name = name;
        //    this.starttime = starttime;
        //    this.club = club;
        //    this.zielzeit = zielzeit;
        //    eventt = e;
        //    this.jahrgang = jahrgang;
        //    this.startnummer = startnummer;
        //    this.rang = rang;

        //}

        public Laeufer(MyCursor cursor) {
            Id = cursor.GetInt(SQLiteHelper.COLUMN_ID);
            Name = cursor.GetString(SQLiteHelper.COLUMN_Name);
            Jahrgang = cursor.GetInt(SQLiteHelper.COLUMN_Jahrgang);
            Club = cursor.GetString(SQLiteHelper.COLUMN_Club);
            Category = cursor.GetString(SQLiteHelper.COLUMN_Category);
            Startnummer = cursor.GetInt(SQLiteHelper.COLUMN_Startnummer);
            Startzeit = cursor.GetDate(SQLiteHelper.COLUMN_Startzeit);
            Zielzeit = cursor.GetDate(SQLiteHelper.COLUMN_Zielzeit);
            Rang = cursor.GetInt(SQLiteHelper.COLUMN_Rang);
        }

        public int Id { get; }
        public string Name { get; }
        public int Jahrgang { get; }
        public string Club { get; }
        public string Category { get; }
        public int Startnummer { get; }
        public DateTime Startzeit { get; }
        public DateTime Zielzeit { get; }
        public int Rang { get; }


        //public string StartzeitString() {
        //    return "(" + starttime + " - " + category + ": " + name + ")";
        //}

        //public string ZielzeitString() {
        //    return "(" + rang + ". - " + category + ": " + name + ": " + zielzeit + ")";
        //}

        //public bool CompareClub(string club) {
        //    if(string.IsNullOrWhiteSpace(this.club) || string.IsNullOrWhiteSpace(club)) {
        //        return false;
        //    }
        //    return club.Contains(this.club);
        //}

        //public bool CompareFriend(string friend) {
        //    return name.Contains(friend);
        //}
    }
}
