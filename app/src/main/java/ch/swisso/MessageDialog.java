package ch.swisso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MessageDialog extends DialogFragment {

    private String content;
    private String title;
    private Daten daten;


    public void init(Daten daten, String content, String title) {
        this.content = content;
        this.title = title;
        this.daten = daten;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setMessage(content);

        builder.setPositiveButton(R.string.gelesen, (dialog, i) -> {
            daten.updateAsRead();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, i) -> dialog.dismiss());
        setCancelable(true);
        return builder.create();
    }
}


