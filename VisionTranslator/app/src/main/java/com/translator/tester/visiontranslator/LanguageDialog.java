package com.translator.tester.visiontranslator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class LanguageDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Languages");
        builder.setItems(R.array.languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TranslateURL.TranslateConfig(i);
                Toast.makeText(getActivity(), "Translate to "+TranslateURL.langs[i], Toast.LENGTH_LONG).show();
            }
        });

        Dialog dialog = builder.create();

        return dialog;
    }
}
