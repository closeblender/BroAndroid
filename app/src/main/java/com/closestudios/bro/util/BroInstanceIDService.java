package com.closestudios.bro.util;

import android.util.Log;

import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by closestudios on 12/1/15.
 */
public class BroInstanceIDService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Clear the old one
        BroPreferences.getPrefs(BroApplication.getContext()).clearGCMId();

        // Call SignInToken
        if(BroPreferences.getPrefs(BroApplication.getContext()).hasToken()) {

            ServerApi.getApi().createNewRequest().signIn(BroPreferences.getPrefs(BroApplication.getContext()).getToken(), new ServerApiCalls.SignInCallback() {
                @Override
                public void onSuccess(String token, String broName) {
                    // Save Token and Bro Name
                    BroPreferences.getPrefs(BroApplication.getContext()).setToken(token);
                    BroPreferences.getPrefs(BroApplication.getContext()).setBroName(broName);
                }

                @Override
                public void onError(String error) {
                    // Error!
                    Log.d("Token Refresh", error);
                }
            });

        }

    }
}
