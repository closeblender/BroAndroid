package com.closestudios.bro;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            if(BroPreferences.getPrefs(this).getToken() != null) {
                // Attempt to sign in with token
                ServerApi.getApi().createNewRequest().signIn(BroPreferences.getPrefs(this).getToken(), this);
                showSpinner("Signing In", false);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBro:
                BroLocationService.tryStartLocationService(this);
                SignInUpDialogFragment signUpDialog = SignInUpDialogFragment.getInstance(true);
                signUpDialog.show(getSupportFragmentManager(), "sign_up");
                break;
            case R.id.tvSignIn:
                BroLocationService.tryStartLocationService(this);
                SignInUpDialogFragment signInDialog = SignInUpDialogFragment.getInstance(false);
                signInDialog.show(getSupportFragmentManager(), "sign_in");
                break;
        }
    }

    @Override
    public void onSuccess(final String token, final String broName) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissSpinner();
                BroPreferences.getPrefs(SignInActivity.this).setToken(token);
                BroPreferences.getPrefs(SignInActivity.this).setBroName(broName);
                finish();
                startActivity(new Intent(SignInActivity.this, MainMenuActivity.class));
            }
        });

    }

    @Override
    public void onError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissSpinner();
                Toast.makeText(SignInActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

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
