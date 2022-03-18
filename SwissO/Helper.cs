using System;

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
            bool success =  DateTime.TryParse(s, out DateTime result);
            return success ? result : DateTime.MinValue;
        }
    }
}
