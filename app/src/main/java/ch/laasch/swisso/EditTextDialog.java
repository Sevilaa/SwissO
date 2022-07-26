package ch.laasch.swisso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(v);

        builder.setTitle(club ? R.string.club_add : R.string.friend_add);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            EditText et = v.findViewById(R.id.dialog_name);
            String name = et.getText().toString();
            fragment.editTextDialogResult(club, name);
            dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dismiss());
        setCancelable(true);
        return builder.create();
    }
}
