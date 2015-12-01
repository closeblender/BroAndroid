package com.closestudios.bro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroHub;
import com.closestudios.bro.util.BroPreferences;

/**
 * Created by closestudios on 11/24/15.
 */
public class ModifyBroDialogFragment extends DialogFragment {

    public enum ModifyBroType {
        Add(0), Remove(1), Block(2);

        private final int value;
        private ModifyBroType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    static String arg0 = "type";

    public static ModifyBroDialogFragment getInstance(ModifyBroType modifyType) {
        ModifyBroDialogFragment modifyBroDialogFragment = new ModifyBroDialogFragment();
        Bundle data = new Bundle();
        data.putInt(arg0, modifyType.getValue());
        modifyBroDialogFragment.setArguments(data);
        return modifyBroDialogFragment;
    }

    EditText etBroName;
    ModifyBroType modifyBroType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_bro, null);

        modifyBroType = ModifyBroType.Add;
        if (getArguments() != null && getArguments().containsKey(arg0)) {
            modifyBroType = ModifyBroType.values()[getArguments().getInt(arg0)];
        }

        final TextView tvHeader = (TextView)view.findViewById(R.id.tvHeader);
        TextView tvInvite = (TextView)view.findViewById(R.id.tvInvite);
        switch (modifyBroType) {
            case Add:
                tvHeader.setText("Add A Bro");
                tvInvite.setText("Add");
                break;
            case Remove:
                tvHeader.setText("Remove A Bro");
                tvInvite.setText("Remove");
                break;
            case Block:
                tvHeader.setText("Block A Bro");
                tvInvite.setText("Block");
                break;
        }

        etBroName = (EditText)view.findViewById(R.id.etBroName);

        TextView tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etBroName.getText().toString().length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_base_animation);
                    etBroName.startAnimation(shake);
                    return;
                }

                switch (modifyBroType) {
                    case Add:
                        Toast.makeText(getActivity(), "Adding Bro", Toast.LENGTH_SHORT).show();
                        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).addBro(etBroName.getText().toString());
                        break;
                    case Remove:
                        Toast.makeText(getActivity(), "Removing Bro", Toast.LENGTH_SHORT).show();
                        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).removeBro(etBroName.getText().toString());
                        break;
                    case Block:
                        Toast.makeText(getActivity(), "Blocking Bro", Toast.LENGTH_SHORT).show();
                        BroApplication.getBroHub(BroPreferences.getPrefs(getActivity()).getToken()).blockBro(etBroName.getText().toString());
                        break;
                }


                dismiss();

            }
        });

        builder.setView(view);
        return builder.create();
    }

}