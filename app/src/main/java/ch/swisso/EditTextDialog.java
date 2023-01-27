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

    private final boolean club;
    private final ProfilFragment fragment;

    private View v;

    public EditTextDialog(boolean club, ProfilFragment fragment) {
        this.club = club;
        this.fragment = fragment;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getAct());
        LayoutInflater inflater = fragment.getAct().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(v);
        EditText et = v.findViewById(R.id.dialog_name);
        builder.setTitle(club ? R.string.club_add : R.string.friend_add);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String name = et.getText().toString();
            fragment.editTextDialogResult(club, name);
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
