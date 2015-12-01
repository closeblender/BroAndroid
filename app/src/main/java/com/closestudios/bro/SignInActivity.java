package com.closestudios.bro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.closestudios.bro.util.BroPreferences;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, ServerApiCalls.SignInCallback,
        SpinnerDialogFragment.CancelListener {

    @InjectView(R.id.tvBro)
    TextView tvBro;
    @InjectView(R.id.tvSignIn)
    TextView tvSignIn;

    SpinnerDialogFragment spinnerDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.inject(this);

        tvBro.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);

        if(BroPreferences.getPrefs(this).getToken() != null) {
            // Attempt to sign in with token
            ServerApi.getApi().createNewRequest().signIn(BroPreferences.getPrefs(this).getToken(), this);
            showSpinner("Signing In", false);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBro:
                SignInUpDialogFragment signUpDialog = SignInUpDialogFragment.getInstance(true);
                signUpDialog.show(getSupportFragmentManager(), "sign_up");
                break;
            case R.id.tvSignIn:
                SignInUpDialogFragment signInDialog = SignInUpDialogFragment.getInstance(false);
                signInDialog.show(getSupportFragmentManager(), "sign_in");
                break;
        }
    }

    @Override
    public void onSuccess(String token, String broName) {
        dismissSpinner();
        BroPreferences.getPrefs(this).setToken(token);
        BroPreferences.getPrefs(this).setBroName(broName);
        finish();
        startActivity(new Intent(this, MainMenuActivity.class));
    }

    @Override
    public void onError(String error) {
        dismissSpinner();
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    public void showSpinner(String title, boolean cancelable) {
        dismissSpinner();

        spinnerDialogFragment = SpinnerDialogFragment.getInstance(title, cancelable);
        spinnerDialogFragment.show(getSupportFragmentManager(), "loading");
    }

    public void dismissSpinner() {
        if(spinnerDialogFragment != null) {
            spinnerDialogFragment.dismiss();
            spinnerDialogFragment = null;
        }
    }

    @Override
    public void onCancel() {
        dismissSpinner();
    }
}
