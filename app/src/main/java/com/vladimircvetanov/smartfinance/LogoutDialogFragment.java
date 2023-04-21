package com.vladimircvetanov.smartfinance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.vladimircvetanov.smartfinance.db.DBAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class LogoutDialogFragment extends DialogFragment {

    private Context context;
    private DBAdapter adapter;

    public LogoutDialogFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = DBAdapter.getInstance(context);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(context.getString(R.string.logout_title));
        alertDialogBuilder.setMessage(context.getString(R.string.logout_message));

        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setPositiveButton(context.getString(R.string.logout_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.clearCache();
                context.startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return alertDialogBuilder.create();
    }
}
