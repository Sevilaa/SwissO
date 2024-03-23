package ch.swisso;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditTextDialog extends DialogFragment {

    private final ProfilFragment.ProfilList list;
    private final ProfilFragment fragment;

    public EditTextDialog(ProfilFragment.ProfilList list, ProfilFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getAct());
        LayoutInflater inflater = fragment.getAct().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(v);
        EditText et = v.findViewById(R.id.dialog_name);
        builder.setTitle(list == ProfilFragment.ProfilList.Club ? R.string.club_add : (list == ProfilFragment.ProfilList.Freund ? R.string.friend_add : R.string.kat_add));
        builder.setPositiveButton(R.string.okay, (dialog, which) -> {
            String name = et.getText().toString();
            fragment.editTextDialogResult(list, name);
            dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dismiss());
        setCancelable(true);

        et.requestFocus();
        Dialog d = builder.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return d;
    }
}
