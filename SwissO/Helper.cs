using System;

namespace SwissO {
    public abstract class Helper {
        public const int intnull = 23904857;

        public const int selectionablesLength = 5;

        public interface EntryPortal {
            public const int None = 0;
            public const int Go2ol = 1;
            public const int Pico = 2;
            public const int Other = 3;
            public const int OLEvents = 5;
        }

        //public enum Maps { Google, GoogleSat, Search, OSM, Swisstopo }

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

        public static Uri GetMapsUrl(double intn, double inte) {
            if (intn != intnull && inte != intnull) {
                return new Uri("geo:" + intn + ","+ inte +"?q=" + intn + "," + inte + "(WKZ)");
                //return maps switch {
                //    Maps.Google => new Uri("https://maps.google.com/maps?q=" + intn + "," + inte),
                //    Maps.GoogleSat => new Uri(GetMapsUrl(Maps.Google, swissn, swisse) + "&t=h"),
                //    Maps.Swisstopo => new Uri("http://map.geo.admin.ch/?Y=" + swisse + "&X=" + swissn + "&crosshair=circle&zoom=6"),
                //    Maps.OSM => new Uri("http://www.openstreetmap.org/?mlat=" + intn + "&mlon=" + inte + "#map=12/" + intn + "/" + inte),
                //    Maps.Search => new Uri("http://map.search.ch/" + swisse + "," + swissn),
                //    _ => null,
                //};
            }
            return null;
        }

        public static DateTime GetDate(string s) {
            if (s == "") {
                return DateTime.MinValue;
            }
            DateTime result;
            bool success =  DateTime.TryParse(s, out result);
            return success ? result : DateTime.MinValue;
        }
    }
}
