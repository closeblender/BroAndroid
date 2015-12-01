package com.closestudios.bro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by closestudios on 11/23/15.
 */
public class SpinnerDialogFragment extends DialogFragment {

    static String arg0 = "title";
    static String arg1 = "cancelable";

    public static SpinnerDialogFragment getInstance(String title, boolean cancelable) {
        SpinnerDialogFragment spinnerDialogFragment = new SpinnerDialogFragment();
        Bundle data = new Bundle();
        data.putString(arg0, title);
        data.putBoolean(arg1, cancelable);
        spinnerDialogFragment.setArguments(data);
        spinnerDialogFragment.setCancelable(cancelable);
        return spinnerDialogFragment;
    }

    public interface CancelListener {
        public void onCancel();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_spinner, null);

        String title = "Title";
        if (getArguments() != null && getArguments().containsKey(arg0)) {
            title = getArguments().getString(arg0);
        }

        boolean cancelabe = true;
        if (getArguments() != null && getArguments().containsKey(arg1)) {
            cancelabe = getArguments().getBoolean(arg1);
        }

        TextView tvHeader = (TextView)view.findViewById(R.id.tvHeader);
        tvHeader.setText(title);

        TextView tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvCancel.setVisibility(cancelabe ? View.VISIBLE : View.GONE);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof CancelListener) {
                    ((CancelListener)getActivity()).onCancel();
                }
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

}