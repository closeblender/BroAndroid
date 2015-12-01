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

import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignInUpDialogFragment extends DialogFragment {


    static String arg0 = "sign_up";

    EditText etUsername;
    EditText etPassword;
    boolean signUp;

    public static SignInUpDialogFragment getInstance(boolean signUp) {
        SignInUpDialogFragment signUpDialogFragment = new SignInUpDialogFragment();
        Bundle data = new Bundle();
        data.putBoolean(arg0, signUp);
        signUpDialogFragment.setArguments(data);
        return signUpDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sign_up, null);

        signUp = true;
        if (getArguments() != null && getArguments().containsKey(arg0)) {
            signUp = getArguments().getBoolean(arg0);
        }


        etUsername = (EditText)view.findViewById(R.id.etUsername);
        etPassword = (EditText)view.findViewById(R.id.etPassword);

        TextView tvHeader = (TextView)view.findViewById(R.id.tvHeader);
        tvHeader.setText(signUp ? "Sign Up" : "Sign In");

        TextView tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvSignUp = (TextView)view.findViewById(R.id.tvSignUp);
        tvSignUp.setText(signUp ? "Sign Up" : "Sign In");
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etUsername.getText().toString().length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_base_animation);
                    etUsername.startAnimation(shake);
                    return;
                }

                if(etPassword.getText().toString().length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake_base_animation);
                    etPassword.startAnimation(shake);
                    return;
                }

                if(!(getActivity() instanceof SignInActivity)) {

                    // Sign In/ Sign Up
                    if(signUp) {
                        ServerApi.getApi().createNewRequest().signUp(etUsername.getText().toString(), etPassword.getText().toString(), (SignInActivity)getActivity());
                    } else {
                        ServerApi.getApi().createNewRequest().signIn(etUsername.getText().toString(), etPassword.getText().toString(), (SignInActivity)getActivity());
                    }

                    dismiss();

                    ((SignInActivity) getActivity()).showSpinner(signUp ? "Signing Up" : "Signing In", false);

                }

            }
        });

        builder.setView(view);
        return builder.create();
    }
}
