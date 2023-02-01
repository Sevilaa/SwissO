package ch.swisso;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SortierDialog extends DialogFragment {

    private static final String[] startlistColumns = {SQLiteHelper.COLUMN_STARTNUMMER, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_KATEGORIE, SQLiteHelper.COLUMN_STARTZEIT};
    private static final String[] ranglistColumns = {SQLiteHelper.COLUMN_RANG, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_KATEGORIE, SQLiteHelper.COLUMN_ZIELZEIT};

    private final ListFragment listFragment;

    private String selectedColumn;


    public SortierDialog(ListFragment listFragment) {
        this.listFragment = listFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(listFragment.getAct());
        SharedPreferences pref = listFragment.getAct().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);



        if (listFragment.isStartliste()) {
            String saved = pref.getString(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
            int selected = 0;
            while (selected < startlistColumns.length && !startlistColumns[selected].equals(saved)) {
                selected++;
            }
            builder.setSingleChoiceItems(R.array.sorting_startlist_entires, selected, (dialog, which) -> selectedColumn = startlistColumns[which]);
            selectedColumn = startlistColumns[selected];
        }
        if (listFragment.isRangliste()) {
            String saved = pref.getString(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
            int selected = 0;
            while (selected < ranglistColumns.length && !ranglistColumns[selected].equals(saved)) {
                selected++;
            }
            builder.setSingleChoiceItems(R.array.sorting_ranglist_entires, selected, (dialog, which) -> selectedColumn = ranglistColumns[which]);
            selectedColumn = ranglistColumns[selected];
        }

        builder.setTitle(R.string.sortierung);
        builder.setPositiveButton(R.string.aufsteigend, (dialog, which) -> save(true));
        builder.setNegativeButton(R.string.absteigend, (dialog, which) -> save(false));
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> dismiss());
        setCancelable(true);
        return builder.create();
    }

    private void save(boolean aufsteigend) {
        SharedPreferences.Editor editor = listFragment.getAct().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE).edit();
        editor.putBoolean(listFragment.isStartliste() ? Helper.Keys.sorting_startlist_ascending : Helper.Keys.sorting_ranglist_ascending, aufsteigend);
        editor.putString(listFragment.isStartliste() ? Helper.Keys.sorting_startlist_column : Helper.Keys.sorting_ranglist_column, selectedColumn);
        editor.apply();
        listFragment.triggerSingleList();
        dismiss();
    }
}
