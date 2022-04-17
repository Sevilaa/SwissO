using Android.Content;
using Android.Database.Sqlite;

namespace SwissO.Droid {
    class SQLiteHelper_A : SQLiteOpenHelper, SQLiteHelper {

        public SQLiteHelper_A(Context context) : base(context, SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION) {
        }

        public override void OnCreate(SQLiteDatabase db) {
            db.ExecSQL(SQLiteHelper.SQL_Profil);
            db.ExecSQL(SQLiteHelper.SQL_Freunde);
            db.ExecSQL(SQLiteHelper.SQL_Clubs);
            db.ExecSQL(SQLiteHelper.SQL_Events);
            db.ExecSQL(SQLiteHelper.SQL_Laeufer);
        }

        public override void OnUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 3) {
                db.ExecSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_Profil); 
                db.ExecSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_Laeufer);
            }
            OnCreate(db);
        }
    }
}