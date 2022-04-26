using System;
using System.Globalization;
using static SwissO.MyResources;

namespace SwissO {
    public abstract class Helper {
        public const int intnull = 23904857;

        public const int selectionablesLength = 5;

        public const string pref_file = "default_pref";
        public const string original = "original";

        public interface Keys {
            public const string sorting_startlist_column = "sorting_startlist_column";
            public const string sorting_startlist_ascending = "sorting_startlist_ascending";
            public const string sorting_ranglist_column = "sorting_ranglist_column";
            public const string sorting_ranglist_ascending = "sorting_ranglist_ascending";
        }

        public interface Defaults {
            public const string sorting_startlist_column = SQLiteHelper.COLUMN_Startnummer;
            public const bool sorting_startlist_ascending = true;
            public const string sorting_ranglist_column = SQLiteHelper.COLUMN_Rang;
            public const bool sorting_ranglist_ascending = true;

        }

        public sealed class Disqet {
            public static TimeSpan postenFalsch = new TimeSpan(2000, 1, 1);
            public static TimeSpan aufgegeben = new TimeSpan(2000, 1, 2);
            public static TimeSpan postenFehlt = new TimeSpan(2000, 1, 3);
            public static TimeSpan disqet = new TimeSpan(2000, 1, 4);
            public static TimeSpan dns = new TimeSpan(2000, 1, 5);
            public static TimeSpan ueberzeit = new TimeSpan(2000, 1, 6);
            public static TimeSpan nichtKlassiert = new TimeSpan(2000, 1, 7);
        }

        public static TimeSpan GetZielzeit(string time) {
            switch (time) {
                case "Po.f.":
                case "Po.fal.":
                case "mp":
                    return Disqet.postenFalsch;
                case "P.fehl.":
                    return Disqet.postenFehlt;
                case "aufgeg.":
                    return Disqet.aufgegeben;
                case "Überzt.":
                    return Disqet.ueberzeit;
                case "n.kl.":
                    return Disqet.nichtKlassiert;
                case "disqu.":
                    return Disqet.disqet;
                default:
                    bool success = TimeSpan.TryParseExact(time, new string[] { @"h\:mm\:ss", @"m\:ss" }, new CultureInfo("de-CH"), TimeSpanStyles.None, out TimeSpan ziel);
                    if (!success) {
                        ziel = TimeSpan.MinValue;
                    }
                    return ziel;
            }
        }

        public static string GetZielzeit(TimeSpan time, MyResources res) {
            if (time == Disqet.postenFalsch)
                return res.GetString(StringResource.PostenFalsch);
            if (time == Disqet.dns)
                return res.GetString(StringResource.DNS);
            if (time == Disqet.disqet)
                return res.GetString(StringResource.Disqet);
            if (time == Disqet.postenFehlt)
                return res.GetString(StringResource.PostenFehlt);
            if (time == Disqet.aufgegeben)
                return res.GetString(StringResource.Aufgegeben);
            if (time == Disqet.nichtKlassiert)
                return res.GetString(StringResource.nichtKlassiert);
            if (time == Disqet.ueberzeit)
                return res.GetString(StringResource.Ueberzeit);
            return time.ToString(@"h\:mm\:ss").TrimStart('0').TrimStart(':');

        }




        public interface EntryPortal {
            public const int None = 0;
            public const int Go2ol = 1;
            public const int Pico = 2;
            public const int Other = 3;
            public const int OLEvents = 5;
        }

        public static (double, double) CalcSwiss(double swissnord, double swisseast) {
            double y = (swisseast - 600000) / 1000000;
            double x = (swissnord - 200000) / 1000000;
            double inteast = 2.6779094;
            inteast += 4.728982 * y;
            inteast += 0.791484 * y * x;
            inteast += 0.1306 * y * x * x;
            inteast -= 0.0436 * y * y * y;
            inteast *= 100.0 / 36.0;
            double intnord = 16.9023892;
            intnord += 3.238272 * x;
            intnord -= 0.270978 * y * y;
            intnord -= 0.002528 * x * x;
            intnord -= 0.0447 * y * y * x;
            intnord -= 0.0140 * x * x * x;
            intnord *= 100.0 / 36.0;
            return (intnord, inteast);
        }

        public static int GetInt(string s) {
            if (string.IsNullOrWhiteSpace(s)) {
                return intnull;
            }
            bool success = int.TryParse(s, out int i);
            return success ? i : intnull;
        }

        public static DateTime GetDate(string s) {
            if (string.IsNullOrWhiteSpace(s)) {
                return DateTime.MinValue;
            }
            bool success = DateTime.TryParse(s, out DateTime result);
            return success ? result : DateTime.MinValue;
        }
    }
}
