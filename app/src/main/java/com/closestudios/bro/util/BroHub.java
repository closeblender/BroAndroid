package com.closestudios.bro.util;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.ServerApi;
import com.closestudios.bro.networking.ServerApiCalls;

import java.util.ArrayList;

/**
 * Created by closestudios on 11/24/15.
 */
public class BroHub implements ServerApiCalls.BroCallback{

    ArrayList<BroHubListener> listeners = new ArrayList<>();
    public String token;

    Bro[] brosCache;
    boolean getBroRequestActive = false;

    public BroHub(String token) {
        this.token = token;
    }

    public void updateToken(String newToken) {
        if(token == null || !token.equals(newToken)) {
            brosCache = null;
            token = newToken;
            if(token != null) {
                getBros(null, false);
            }
        } else {
            token = newToken;
        }
    }

    @Override
    public void onSuccess(Bro[] bros) {
        getBroRequestActive = false;
        brosCache = bros;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != null) {
                listeners.get(i).onReceiveBros(brosCache);
            }
        }
    }

    @Override
    public void onError(String error) {
        getBroRequestActive = false;
        brosCache = null;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != null) {
                listeners.get(i).onReceiveBrosFailed(error);
            }
        }
    }

    public void triggerGettingBros() {
        getBroRequestActive = true;
        for(int i=0;i<listeners.size();i++) {
            if(listeners.get(i) != null) {
                listeners.get(i).onGettingBros();
            }
        }
    }


    public interface BroHubListener {
        void onGettingBros();
        void onReceiveBros(Bro[] bros);
        void onReceiveBrosFailed(String error);
    }

    public void subscribe(BroHubListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    public void unsubscribe(BroHubListener listener) {
        listeners.remove(listener);
    }

    public void getBros(final BroHubListener listener, boolean useCache) {
        if(useCache && brosCache != null) {
            listener.onReceiveBros(brosCache);
            return;
        }


        if(!getBroRequestActive) {
            // We need to load bros!
            triggerGettingBros();
            ServerApi.getApi().createNewRequest().getBros(token, new ServerApiCalls.BroCallback() {
                @Override
                public void onSuccess(Bro[] bros) {
                    BroHub.this.onSuccess(bros);
                }

                @Override
                public void onError(String error) {
                    BroHub.this.onError(error);
                }
            });
        }

    }

    public void addBro(String broName) {
        triggerGettingBros();
        ServerApi.getApi().createNewRequest().addBro(token, broName, this);
    }
    public void removeBro(String broName) {
        triggerGettingBros();
        ServerApi.getApi().createNewRequest().removeBro(token, broName, this);
    }
    public void blockBro(String broName) {
        triggerGettingBros();
        ServerApi.getApi().createNewRequest().blockBro(token, broName, this);
    }

    public Bro[] getBrosCache() {
        return brosCache;
    }

    public boolean isGettingBros() {
        return getBroRequestActive;
    }

}
