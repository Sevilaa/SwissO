namespace SwissO {
    interface SQLiteHelper {
        public const string DATABASE_NAME = "SwissO.dat";

        //Tables
        public const string TABLE_Profil = "Profil";
        public const string TABLE_Freunde = "Friends";
        public const string TABLE_Clubs = "Clubs";
        public const string TABLE_Events = "Events";

        //Primary Key
        public const string COLUMN_ID = "_id";

        //Fremdschlüssel
        public const string COLUMN_Profil = "profil";

        //Table Profil
        public const string COLUMN_Vorname = "first_name";
        public const string COLUMN_Nachname = "surname";
        public const string COLUMN_Name = "name";
        public const string COLUMN_SI = "sicard";
        public const string COLUMN_Category = "category";

        //Table Events
        public const string COLUMN_Title = "title";
        public const string COLUMN_SOLVId = "solvid";
        public const string COLUMN_Date = "date";
        public const string COLUMN_Deadline = "deadline";
        public const string COLUMN_Region = "region";
        public const string COLUMN_SKoordN = "skoordn";
        public const string COLUMN_SKoordE = "skoorde";
        public const string COLUMN_Club = "club";
        public const string COLUMN_Map = "map";
        public const string COLUMN_LAusschreibung = "ausschreibung";
        public const string COLUMN_LWeisungen = "weisungen";
        public const string COLUMN_LRangliste = "rangliste";
        public const string COLUMN_LLiveRangliste = "liverangliste";
        public const string COLUMN_LStartliste = "startliste";
        public const string COLUMN_LAnmeldung = "anmeldung";
        public const string COLUMN_LMutation = "mutation";
        public const string COLUMN_EntryPortal = "entryportal";


        protected const string SQL_Profil = "CREATE TABLE IF NOT EXISTS " + TABLE_Profil + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_Vorname + " VARCHAR(15) NOT NULL," +
                COLUMN_Nachname + " VARCHAR(15) NOT NULL," +
                COLUMN_SI + " INTEGER NOT NULL," +
                COLUMN_Category + " VARCHAR(15) NOT NULL)";

        protected const string SQL_Freunde = "CREATE TABLE IF NOT EXISTS " + TABLE_Freunde + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_Vorname + " VARCHAR(15) NOT NULL," +
                COLUMN_Nachname + " VARCHAR(15) NOT NULL," +
                COLUMN_Profil + " INTEGER NOT NULL)";

        protected const string SQL_Clubs = "CREATE TABLE IF NOT EXISTS " + TABLE_Clubs + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_Name + " VARCHAR(31) NOT NULL," +
                COLUMN_Profil + " INTEGER NOT NULL)";

        protected const string SQL_Events = "CREATE TABLE IF NOT EXISTS " + TABLE_Events + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_Title + " VARCHAR(15) NOT NULL," +
                COLUMN_Date + " INTEGER NOT NULL," +
                COLUMN_Region + " VARCHAR(15)," +
                COLUMN_Club + " VARCHAR(31)," +
                COLUMN_Map + " VARCHAR(31)," +
                COLUMN_Deadline + " INTEGER," +
                COLUMN_SKoordN + " INTEGER NOT NULL," +
                COLUMN_SKoordE + " INTEGER NOT NULL," +
                COLUMN_LAusschreibung + " VARCHAR(255)," +
                COLUMN_LWeisungen + " VARCHAR(255)," +
                COLUMN_LRangliste + " VARCHAR(255)," +
                COLUMN_LLiveRangliste + " VARCHAR(255)," +
                COLUMN_LStartliste + " VARCHAR(255)," +
                COLUMN_LAnmeldung + " VARCHAR(255)," +
                COLUMN_LMutation + " VARCHAR(255)," +
                COLUMN_EntryPortal + " INTEGER NOT NULL)";
    }
}
