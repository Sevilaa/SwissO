using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SwissO.Droid {
    internal class SortingDialog : AndroidX.Fragment.App.DialogFragment, IDialogInterfaceOnClickListener {

        private readonly ListManager.ListType listType;
        private readonly ListManager manager;

        private static readonly string[] startlistColumns = { Helper.original, SQLiteHelper.COLUMN_Startnummer, SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category, SQLiteHelper.COLUMN_Startzeit};
        private static readonly string[] ranglistColumns = { Helper.original, SQLiteHelper.COLUMN_Rang, SQLiteHelper.COLUMN_Name, SQLiteHelper.COLUMN_Category, SQLiteHelper.COLUMN_Zielzeit };

        private string selectedColumn;

        public SortingDialog(ListManager manager) {
            listType = manager.GetListType();
            this.manager = manager;
        }

        public override Dialog OnCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
            ISharedPreferences pref = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private);
            if(listType == ListManager.ListType.Startliste) {
                string saved = pref.GetString(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
                int selected = Array.IndexOf(startlistColumns, saved);
                builder.SetSingleChoiceItems(Resource.Array.sorting_startlist_entires, selected, this);
                OnClick(null, selected);
            }
            if (listType == ListManager.ListType.Rangliste) {
                string saved = pref.GetString(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
                int selected = Array.IndexOf(ranglistColumns, saved);
                builder.SetSingleChoiceItems(Resource.Array.sorting_ranglist_entires, selected, this);
                OnClick(null, selected);
            }

            builder.SetTitle(Resource.String.sortierung);
            builder.SetPositiveButton(Resource.String.aufsteigend, new EventHandler<DialogClickEventArgs>(Aufsteigend));
            builder.SetNegativeButton(Resource.String.absteigend, new EventHandler<DialogClickEventArgs>(Absteigend));
            builder.SetNeutralButton(Resource.String.cancel, new EventHandler<DialogClickEventArgs>(CancelClick));
            Cancelable = true;
            return builder.Create();
        }


        private void Aufsteigend(object sender, DialogClickEventArgs e) {
            Save(true);
        }

        private void Absteigend(object sender, DialogClickEventArgs e) {
            Save(false);
        }

        private void Save(bool aufsteigend) {
            ISharedPreferencesEditor editor = Context.GetSharedPreferences(Helper.pref_file, FileCreationMode.Private).Edit();
            editor.PutBoolean(listType == ListManager.ListType.Startliste ? Helper.Keys.sorting_startlist_ascending : Helper.Keys.sorting_ranglist_ascending, aufsteigend);
            editor.PutString(listType == ListManager.ListType.Startliste ? Helper.Keys.sorting_startlist_column : Helper.Keys.sorting_ranglist_column, selectedColumn);
            editor.Apply();
            manager.LoadList();
            Dismiss();
        }

        private void CancelClick(object sender, DialogClickEventArgs e) {
            Dismiss();
        }

        public void OnClick(IDialogInterface dialog, int which) {
            selectedColumn = listType == ListManager.ListType.Startliste ? startlistColumns[which] : ranglistColumns[which];
        }
    }
}