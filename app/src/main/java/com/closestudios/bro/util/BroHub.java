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
    String token;

    Bro[] brosCache;
    boolean getBroRequestActive = false;

    public BroHub(String token) {
        this.token = token;
    }

    @Override
    public void onSuccess(Bro[] bros) {
        brosCache = bros;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != null) {
                listeners.get(i).onReceiveBros(brosCache);
            }
        }
    }

    @Override
    public void onError(String error) {
        brosCache = null;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != null) {
                listeners.get(i).onReceiveBrosFailed(error);
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

        // We need to load bros!
        for(int i=0;i<listeners.size();i++) {
            if(listeners.get(i) != null) {
                listeners.get(i).onGettingBros();
            }
        }

        if(!getBroRequestActive) {
            getBroRequestActive  = true;
            ServerApi.getApi().createNewRequest().getBros(token, new ServerApiCalls.BroCallback() {
                @Override
                public void onSuccess(Bro[] bros) {
                    getBroRequestActive = false;
                    BroHub.this.onSuccess(bros);
                }

                @Override
                public void onError(String error) {
                    getBroRequestActive = false;
                    BroHub.this.onError(error);
                }
            });
        }

    }

    public void addBro(String broName) {
        ServerApi.getApi().createNewRequest().addBro(token, broName, this);
    }
    public void removeBro(String broName) {
        ServerApi.getApi().createNewRequest().removeBro(token, broName, this);
    }
    public void blockBro(String broName) {
        ServerApi.getApi().createNewRequest().blockBro(token, broName, this);
    }


}
