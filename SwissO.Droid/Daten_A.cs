using Android.App;
using Android.Content;
using Android.Content.Res;
using Android.Database;
using Android.Database.Sqlite;
using System;
using System.Collections.Generic;
using System.Linq;

namespace SwissO.Droid {

    class MyResources_A : MyResources {

        private readonly Resources res;

        public MyResources_A(Resources res) {
            this.res = res;
        }

        public override string GetString(StringResource name) {
            int id = GetStringId(name);
            return res.GetString(id);
        }

        public int GetStringId(StringResource name) {
            int stringId = Helper.intnull;
            switch (name) {
                case StringResource.Anmeldung:
                    stringId = Resource.String.anmeldung;
                    break;
                case StringResource.Ausschreibung:
                    stringId = Resource.String.ausschreibung;
                    break;
                case StringResource.Weisungen:
                    stringId = Resource.String.weisungen;
                    break;
                case StringResource.Mutation:
                    stringId = Resource.String.mutation;
                    break;
                case StringResource.Wkz:
                    stringId = Resource.String.wkz;
                    break;
                case StringResource.Startlist:
                    stringId = Resource.String.startlist;
                    break;
                case StringResource.Liveresult:
                    stringId = Resource.String.liveresult;
                    break;
                case StringResource.Rangliste:
                    stringId = Resource.String.rangliste;
                    break;
                case StringResource.PostenFalsch:
                    stringId = Resource.String.postenfalsch;
                    break;
                case StringResource.PostenFehlt:
                    stringId = Resource.String.postenfehlt;
                    break;
                case StringResource.Aufgegeben:
                    stringId = Resource.String.aufgegeben;
                    break;
                case StringResource.DNS:
                    stringId = Resource.String.dns;
                    break;
                case StringResource.Ueberzeit:
                    stringId = Resource.String.ueberzeit;
                    break;
                case StringResource.nichtKlassiert:
                    stringId = Resource.String.nichtklassiert;
                    break;
                case StringResource.Disqet:
                    stringId = Resource.String.disqet;
                    break;

            }
            if (stringId != Helper.intnull) {
                return stringId;
            }
            throw new NotImplementedException("String " + name.ToString() + " not implemented in MyResource_A");
        }
    }

    class MyCursor_A : MyCursor {

        private readonly ICursor cursor;
        private bool firstRowRead = false;

        public MyCursor_A(ICursor c) {
            cursor = c;
            cursor.MoveToFirst();
        }

        public override void Close() {
            cursor.Close();
        }

        public override float GetFloat(string column) {
            if (IsNull(column)) {
                return Helper.intnull;
            }
            int index = cursor.GetColumnIndex(column);
            return cursor.GetFloat(index);
        }

        public override double GetDouble(string column) {
            if (IsNull(column)) {
                return Helper.intnull;
            }
            int index = cursor.GetColumnIndex(column);
            return cursor.GetDouble(index);
        }

        public override int GetInt(string column) {
            if (IsNull(column)) {
                return Helper.intnull;
            }
            int index = cursor.GetColumnIndex(column);
            return cursor.GetInt(index);
        }

        public override long GetLong(string column) {
            if (IsNull(column)) {
                return Helper.intnull;
            }
            int index = cursor.GetColumnIndex(column);
            return cursor.GetLong(index);
        }

        public override string GetString(string column) {
            int index = cursor.GetColumnIndex(column);
            return cursor.GetString(index);
        }

        public override bool IsNull(string column) {
            int index = cursor.GetColumnIndex(column);
            return cursor.GetType(index) == FieldType.Null;
        }

        public override int Length() {
            return cursor.Count;
        }

        public override bool Read() {
            if (firstRowRead) {
                cursor.MoveToNext();
            }
            else {
                firstRowRead = true;
            }
            return !cursor.IsAfterLast;
        }

        public ICursor Get() {
            return cursor;
        }
    }

    public class Daten_A : Daten {

        private SQLiteDatabase database;
        private readonly SQLiteHelper_A dbHelper;

        public Daten_A(Activity act) {
            dbHelper = new SQLiteHelper_A(act);
            Open();
        }

        public void Open() {
            database = dbHelper.WritableDatabase;
        }

        public override void Close() {
            dbHelper.Close();
        }

        protected override MyCursor Query(string table, string where, string[] columns, string orderBy) {
            return new MyCursor_A(database.Query(table, columns, where, null, null, null, orderBy));
        }

        protected override int Insert(string table, MyContentValues contentValues) {
            ContentValues daten = ConvertContentValues(contentValues);
            return (int)database.Insert(table, null, daten);
        }

        protected override void Update(string table, MyContentValues contentValues, string where) {
            ContentValues daten = ConvertContentValues(contentValues);
            database.Update(table, daten, where, null);
        }

        protected override void Delete(string table, string where) {
            database.Delete(table, where, null);
        }

        private static ContentValues ConvertContentValues(MyContentValues myContentValues) {
            ContentValues daten = new ContentValues();
            Dictionary<string, (TypeCode, object)> content = myContentValues.Get();
            string[] columns = content.Keys.ToList().ToArray();
            for (int i = 0; i < columns.Length; i++) {
                string column = columns[i];
                (TypeCode type, object value) = content[column];
                switch (type) {
                    case TypeCode.Int32:
                        daten.Put(column, Convert.ToInt32(value));
                        break;
                    case TypeCode.Single:
                        daten.Put(column, Convert.ToSingle(value));
                        break;
                    case TypeCode.Double:
                        daten.Put(column, Convert.ToDouble(value));
                        break;
                    case TypeCode.String:
                        daten.Put(column, Convert.ToString(value));
                        break;
                    case TypeCode.Int64:
                        daten.Put(column, Convert.ToInt64(value));
                        break;
                    default:
                        throw new NotImplementedException("NotSQLConformType");
                }
            }

            return daten;
        }
    }
}