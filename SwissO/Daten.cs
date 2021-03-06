using System;
using System.Collections.Generic;
using System.Linq;

namespace SwissO {

    public class MyContentValues {

        private readonly Dictionary<string, (TypeCode, object)> values = new Dictionary<string, (TypeCode, object)>();

        public void Put(string column, object value) {
            if (value != null) {
                TypeCode type = Type.GetTypeCode(value.GetType());
                if (type == TypeCode.Int32) {
                    int i = Convert.ToInt32(value);
                    if (i == Helper.intnull) {
                        return;
                    }
                }
                values[column] = (Type.GetTypeCode(value.GetType()), value);
            }
        }

        public Dictionary<string, (TypeCode, object)> Get() {
            return values;
        }

        public (string[], string[]) GetAsStringArray() {
            string[] columns = values.Keys.ToList().ToArray();
            string[] content = new string[columns.Length];
            for (int i = 0; i < columns.Length; i++) {
                string column = columns[i];
                (TypeCode type, object value) = values[column];
                switch (type) {
                    case TypeCode.Int32:
                        content[i] = Convert.ToInt32(value).ToString();
                        break;
                    case TypeCode.Double:
                        content[i] = ((float)Convert.ToDouble(value)).ToString();
                        break;
                    case TypeCode.String:
                        content[i] = "'" + Convert.ToString(value) + "'";
                        break;
                    case TypeCode.Int64:
                        content[i] = Convert.ToInt64(value).ToString();
                        break;
                }
            }

            return (columns, content);
        }
    }

    public abstract class MyCursor {

        public abstract string GetString(string column);

        public abstract int GetInt(string column);

        public abstract long GetLong(string column);

        public abstract float GetFloat(string column);

        public abstract double GetDouble(string column);

        public abstract bool Read();

        public abstract void Close();

        public abstract bool IsNull(string column);

        public abstract int Length();

        public DateTime GetDate(string column) {
            if (IsNull(column) || GetLong(column) == Helper.intnull) {
                return DateTime.MinValue;
            }
            return new DateTime(GetLong(column));
        }

        public TimeSpan GetTime(string column) {
            if (IsNull(column)) {
                return TimeSpan.MinValue;
            }
            return TimeSpan.FromTicks(GetLong(column));
        }

        public Uri GetUri(string column) {
            if (IsNull(column)) {
                return null;
            }
            string s = GetString(column);
            if (s == "") {
                return null;
            }
            try {
                return new Uri(GetString(column));
            }
            catch (Exception) {
                return null;
            }
        }
    }

    public abstract class MyResources {
        public enum StringResource {
            Ausschreibung, Anmeldung, Weisungen, Mutation, Wkz, Startlist, Liveresult, Rangliste, PostenFalsch, Aufgegeben, PostenFehlt, Disqet, DNS, nichtKlassiert, Ueberzeit
        }

        public abstract string GetString(StringResource name);
    }

    public abstract class Daten {

        //Table Profil

        public int InsertProfil(string vorname, string nachname, int si, string category) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Vorname, vorname);
            daten.Put(SQLiteHelper.COLUMN_Nachname, nachname);
            daten.Put(SQLiteHelper.COLUMN_SI, si);
            daten.Put(SQLiteHelper.COLUMN_Category, category);
            return Insert(SQLiteHelper.TABLE_Profil, daten);
        }

        public void UpdateProfil(int id, string vorname, string nachname, int si, string category) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Vorname, vorname);
            daten.Put(SQLiteHelper.COLUMN_Nachname, nachname);
            daten.Put(SQLiteHelper.COLUMN_SI, si);
            daten.Put(SQLiteHelper.COLUMN_Category, category);
            Update(SQLiteHelper.TABLE_Profil, daten, SameID(id));
        }

        public MyCursor GetAllProfile() {
            return Query(SQLiteHelper.TABLE_Profil, null, null, null);
        }

        public Profil CreateProfil() {
            MyCursor cursor = GetAllProfile();
            if (!cursor.Read()) {
                InsertProfil("", "", Helper.intnull, "");
                cursor = GetAllProfile();
            }
            return new Profil(cursor, this);
        }

        //Table Freunde

        public int InsertFreund(string vorname, int profilID) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Name, vorname);
            daten.Put(SQLiteHelper.COLUMN_Profil, profilID);
            return Insert(SQLiteHelper.TABLE_Freunde, daten);
        }

        public MyCursor GetFreundeByProfil(int profilID) {
            return Query(SQLiteHelper.TABLE_Freunde, SQLiteHelper.COLUMN_Profil + " = " + profilID, null, null);
        }

        public void DeleteFreundById(int id) {
            Delete(SQLiteHelper.TABLE_Freunde, SameID(id));
        }

        //Table Laeufer

        public int InsertLaeufer(string name, int jahrgang, string club, string cat,
            int startnummer, TimeSpan startzeit, TimeSpan zielzeit, int rang, Event e) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Jahrgang, jahrgang);
            daten.Put(SQLiteHelper.COLUMN_Name, name);
            daten.Put(SQLiteHelper.COLUMN_Club, club);
            daten.Put(SQLiteHelper.COLUMN_Category, cat);
            daten.Put(SQLiteHelper.COLUMN_Startnummer, startnummer);
            daten.Put(SQLiteHelper.COLUMN_Startzeit, startzeit.Ticks);
            daten.Put(SQLiteHelper.COLUMN_Zielzeit, zielzeit.Ticks);
            daten.Put(SQLiteHelper.COLUMN_Rang, rang);
            daten.Put(SQLiteHelper.COLUMN_Event, e.Id);
            return Insert(SQLiteHelper.TABLE_Laeufer, daten);
        }

        public MyCursor GetAllLaeuferByEvent(Event e, string filter, string order) {
            string where = SQLiteHelper.COLUMN_Event + " = " + e.Id + " AND (" +
                SQLiteHelper.COLUMN_Name + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Club + " LIKE '%" + filter + "%')";
            return Query(SQLiteHelper.TABLE_Laeufer, where, null, order);
        }

        public MyCursor GetClubLaeuferByEvent(Event e, List<string> clubs, string filter, string order) {
            if (clubs.Count == 0) {
                return Query(SQLiteHelper.TABLE_Laeufer, SQLiteHelper.COLUMN_Club + " = 'lkaasdfsjdf'", null, null);
            }
            string where = SQLiteHelper.COLUMN_Event + " = " + e.Id + " AND (" +
                SQLiteHelper.COLUMN_Name + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Club + " LIKE '%" + filter + "%') AND (" +
                SQLiteHelper.COLUMN_Club + " LIKE '%" + clubs[0] + "%'";
            for (int i = 1; i < clubs.Count; i++) {
                where += " OR " + SQLiteHelper.COLUMN_Club + "LIKE '%" + clubs[i] + "%'";
            }
            where += ")";
            return Query(SQLiteHelper.TABLE_Laeufer, where, null, order);
        }

        public MyCursor GetFriendLaeuferByEvent(Event e, List<string> freunde, string filter, string order) {
            if (freunde.Count == 0) {
                return Query(SQLiteHelper.TABLE_Laeufer, SQLiteHelper.COLUMN_Club + " = 'lkaasdfsjdf'", null, null);
            }
            string where = SQLiteHelper.COLUMN_Event + " = " + e.Id + " AND (" +
                SQLiteHelper.COLUMN_Name + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Category + " LIKE '%" + filter + "%' OR " +
                SQLiteHelper.COLUMN_Club + " LIKE '%" + filter + "%') AND (" +
                SQLiteHelper.COLUMN_Name + " LIKE '%" + freunde[0] + "%'";
            for (int i = 1; i < freunde.Count; i++) {
                where += " OR " + SQLiteHelper.COLUMN_Name + " LIKE '%" + freunde[i] + "%'";
            }
            where += ")";
            return Query(SQLiteHelper.TABLE_Laeufer, where, null, order);
        }

        public int GetLaeuferCountByEvent(Event e) {
            return GetAllLaeuferByEvent(e, "", null).Length();
        }

        public void DeleteAllLaeuferByEvent(Event e) {
            Delete(SQLiteHelper.TABLE_Laeufer, SQLiteHelper.COLUMN_Event + " = " + e.Id);
        }

        //Table Clubs

        public int InsertClub(string name, int profilID) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Name, name);
            daten.Put(SQLiteHelper.COLUMN_Profil, profilID);
            return Insert(SQLiteHelper.TABLE_Clubs, daten);
        }

        public MyCursor GetClubsByProfil(int profilID) {
            return Query(SQLiteHelper.TABLE_Clubs, SQLiteHelper.COLUMN_Profil + " = " + profilID, null, null);
        }

        public void DeleteClubById(int id) {
            Delete(SQLiteHelper.TABLE_Clubs, SameID(id));
        }

        //Table Events

        public int InsertEvent(Event e) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Title, e.Title);
            daten.Put(SQLiteHelper.COLUMN_Date, e.Date.Ticks);
            daten.Put(SQLiteHelper.COLUMN_Region, e.Region);
            daten.Put(SQLiteHelper.COLUMN_Club, e.Club);
            daten.Put(SQLiteHelper.COLUMN_Map, e.Map);
            daten.Put(SQLiteHelper.COLUMN_Deadline, e.Deadline.Ticks);
            daten.Put(SQLiteHelper.COLUMN_IntKoordN, e.Koordn);
            daten.Put(SQLiteHelper.COLUMN_IntKoordE, e.Koorde);
            daten.Put(SQLiteHelper.COLUMN_LAusschreibung, UriString(e.Ausschreibung));
            daten.Put(SQLiteHelper.COLUMN_LWeisungen, UriString(e.Weisungen));
            daten.Put(SQLiteHelper.COLUMN_LRangliste, UriString(e.Rangliste));
            daten.Put(SQLiteHelper.COLUMN_LAnmeldung, UriString(e.Anmeldung));
            daten.Put(SQLiteHelper.COLUMN_LMutation, UriString(e.Mutation));
            daten.Put(SQLiteHelper.COLUMN_LLiveRangliste, UriString(e.Liveresultate));
            daten.Put(SQLiteHelper.COLUMN_LStartliste, UriString(e.Startliste));
            daten.Put(SQLiteHelper.COLUMN_EntryPortal, e.Eventportal);
            return Insert(SQLiteHelper.TABLE_Events, daten);
        }

        public void UpdateEvent(Event e) {
            MyContentValues daten = new MyContentValues();
            daten.Put(SQLiteHelper.COLUMN_Title, e.Title);
            daten.Put(SQLiteHelper.COLUMN_Date, e.Date.Ticks);
            daten.Put(SQLiteHelper.COLUMN_Region, e.Region);
            daten.Put(SQLiteHelper.COLUMN_Club, e.Club);
            daten.Put(SQLiteHelper.COLUMN_Map, e.Map);
            daten.Put(SQLiteHelper.COLUMN_Deadline, e.Deadline.Ticks);
            daten.Put(SQLiteHelper.COLUMN_IntKoordN, e.Koordn);
            daten.Put(SQLiteHelper.COLUMN_IntKoordE, e.Koorde);
            daten.Put(SQLiteHelper.COLUMN_LAusschreibung, UriString(e.Ausschreibung));
            daten.Put(SQLiteHelper.COLUMN_LWeisungen, UriString(e.Weisungen));
            daten.Put(SQLiteHelper.COLUMN_LRangliste, UriString(e.Rangliste));
            daten.Put(SQLiteHelper.COLUMN_LAnmeldung, UriString(e.Anmeldung));
            daten.Put(SQLiteHelper.COLUMN_LMutation, UriString(e.Mutation));
            daten.Put(SQLiteHelper.COLUMN_LLiveRangliste, UriString(e.Liveresultate));
            daten.Put(SQLiteHelper.COLUMN_LStartliste, UriString(e.Startliste));
            daten.Put(SQLiteHelper.COLUMN_EntryPortal, e.Eventportal);
            Update(SQLiteHelper.TABLE_Events, daten, SameID(e.Id));
        }

        public MyCursor GetAllEvents() {
            return Query(SQLiteHelper.TABLE_Events, null, null, SQLiteHelper.COLUMN_Date + " ASC;");
        }

        public MyCursor GetTodayEvent() {
            long today = DateTime.Today.Ticks;
            return Query(SQLiteHelper.TABLE_Events, SQLiteHelper.COLUMN_Date + " = " + today, null, SQLiteHelper.COLUMN_Date + " ASC;");

        }

        //Database

        protected abstract MyCursor Query(string table, string where, string[] columns, string orderBy);

        protected abstract int Insert(string table, MyContentValues daten);

        protected abstract void Update(string table, MyContentValues daten, string where);

        protected abstract void Delete(string table, string where);

        public abstract void Close();

        private static string SameID(int id) {
            return SQLiteHelper.COLUMN_ID + " = " + id;
        }

        private static string UriString(Uri uri) {
            return uri?.OriginalString;
        }
    }
}
