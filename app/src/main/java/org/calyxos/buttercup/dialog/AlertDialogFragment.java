package org.calyxos.buttercup.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;

import org.calyxos.buttercup.R;

public class AlertDialogFragment extends DialogFragment {

    private String message;
    public AlertDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
