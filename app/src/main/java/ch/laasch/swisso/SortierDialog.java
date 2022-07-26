package ch.laasch.swisso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class SortierDialog extends DialogFragment {


    private final MainActivity.FragmentType listType;
    private final ListFragment listFragment;

    private static final String[] startlistColumns = {Helper.original, SQLiteHelper.COLUMN_STARTNUMMER, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_KATEGORIE, SQLiteHelper.COLUMN_STARTZEIT};
    private static final String[] ranglistColumns = {Helper.original, SQLiteHelper.COLUMN_RANG, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_KATEGORIE, SQLiteHelper.COLUMN_ZIELZEIT};

    private String selectedColumn;


    public SortierDialog(ListFragment listFragment, MainActivity act) {
        listType = act.getFragmentType();
        this.listFragment = listFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        SharedPreferences pref = getContext().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE);
        if (listType == MainActivity.FragmentType.Startliste) {
            String saved = pref.getString(Helper.Keys.sorting_startlist_column, Helper.Defaults.sorting_startlist_column);
            int selected = 0;
            while(selected < startlistColumns.length && !startlistColumns[selected].equals(saved)){
                selected++;
            }
            builder.setSingleChoiceItems(R.array.sorting_startlist_entires, selected, (dialog, which) -> selectedColumn = startlistColumns[which]);
            selectedColumn = startlistColumns[selected];
        }
        if (listType == MainActivity.FragmentType.Rangliste) {
            String saved = pref.getString(Helper.Keys.sorting_ranglist_column, Helper.Defaults.sorting_ranglist_column);
            int selected = 0;
            while(selected < ranglistColumns.length && !ranglistColumns[selected].equals(saved)){
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
        SharedPreferences.Editor editor = getContext().getSharedPreferences(Helper.pref_file, Context.MODE_PRIVATE).edit();
        editor.putBoolean(listType == MainActivity.FragmentType.Startliste ? Helper.Keys.sorting_startlist_ascending : Helper.Keys.sorting_ranglist_ascending, aufsteigend);
        editor.putString(listType == MainActivity.FragmentType.Startliste ? Helper.Keys.sorting_startlist_column : Helper.Keys.sorting_ranglist_column, selectedColumn);
        editor.apply();
        listFragment.loadList();
        dismiss();
    }
}
