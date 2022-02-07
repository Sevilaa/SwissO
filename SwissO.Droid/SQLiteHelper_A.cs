using Android.Content;
using Android.Database.Sqlite;

namespace SwissO.Droid {
    class SQLiteHelper_A : SQLiteOpenHelper, SQLiteHelper {

        public SQLiteHelper_A(Context context) : base(context, SQLiteHelper.DATABASE_NAME, null, 1) {
        }

        public override void OnCreate(SQLiteDatabase db) {
            db.ExecSQL(SQLiteHelper.SQL_Profil);
            db.ExecSQL(SQLiteHelper.SQL_Freunde);
            db.ExecSQL(SQLiteHelper.SQL_Clubs);
            db.ExecSQL(SQLiteHelper.SQL_Events);
        }

        public override void OnUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}