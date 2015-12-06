package com.closestudios.bro.networking;

import android.util.Log;

import com.closestudios.bro.R;
import com.closestudios.bro.networking.requests.AddBroRequest;
import com.closestudios.bro.networking.requests.BlockBroRequest;
import com.closestudios.bro.networking.requests.GetBroMessageRequest;
import com.closestudios.bro.networking.requests.GetBrosRequest;
import com.closestudios.bro.networking.requests.RemoveBroRequest;
import com.closestudios.bro.networking.requests.SendBroMessageRequest;
import com.closestudios.bro.networking.requests.SignInCredsRequest;
import com.closestudios.bro.networking.requests.SignInTokenRequest;
import com.closestudios.bro.networking.requests.SignUpRequest;
import com.closestudios.bro.networking.requests.UpdateLocationRequest;
import com.closestudios.bro.networking.responses.BroMessageResponse;
import com.closestudios.bro.networking.responses.ErrorResponse;
import com.closestudios.bro.networking.responses.GetBrosResponse;
import com.closestudios.bro.networking.responses.SignInResponse;
import com.closestudios.bro.util.BroApplication;
import com.closestudios.bro.util.BroPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by closestudios on 11/23/15.
 */
public class NetworkServerApiCalls implements ServerApiCalls {

    static String TAG = "NetworkServerAPI";
    //ServerAPIKey:AIzaSyAO39IewFjVE2Mdc4xq3et6j2w0lynoKM4
    //SenderID:227936239117

    NetworkCall networkCall;
    String host;
    int port;

    public NetworkServerApiCalls(NetworkCall networkCall, String host, int port) {
        this.networkCall = networkCall;
        this.host = host;
        this.port = port;
    }

    @Override
    public void signUp(final String broName, final String password, final SignInCallback callback) {
        Log.d(TAG, "Sign Up: " + broName + ", " + password);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Sign Up
                    SignInResponse signInResponse = null;
                    try {
                        String gcmId = getGCMId();
                        signInResponse = (SignInResponse)sendApiMessage(SignUpRequest.createMessage(broName, password, gcmId), new SignInResponse());

                        if(signInResponse != null) {

                            if (signInResponse.getSuccess()) {
                                callback.onSuccess(signInResponse.getToken(), signInResponse.getBroName());
                            } else {
                                callback.onError(signInResponse.getError());
                            }
                        } else {
                            callback.onError("Sign Up Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void signIn(final String broName, final String password, final SignInCallback callback) {
        Log.d(TAG, "Sign In: " + broName + ", " + password);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Sign In
                    SignInResponse signInResponse = null;
                    try {
                        String gcmId = getGCMId();
                        signInResponse = (SignInResponse)sendApiMessage(SignInCredsRequest.createMessage(broName, password, gcmId), new SignInResponse());

                        if(signInResponse != null) {

                            if (signInResponse.getSuccess()) {
                                callback.onSuccess(signInResponse.getToken(), signInResponse.getBroName());
                            } else {
                                callback.onError(signInResponse.getError());
                            }
                        } else {
                            callback.onError("Sign In Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void signIn(final String token, final SignInCallback callback) {
        Log.d(TAG, "Sign In: " + token);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Sign In
                    SignInResponse signInResponse = null;
                    try {
                        String gcmId = getGCMId();
                        signInResponse = (SignInResponse)sendApiMessage(SignInTokenRequest.createMessage(token, gcmId), new SignInResponse());

                        if(signInResponse != null) {

                            if (signInResponse.getSuccess()) {
                                callback.onSuccess(signInResponse.getToken(), signInResponse.getBroName());
                            } else {
                                callback.onError(signInResponse.getError());
                            }
                        } else {
                            callback.onError("Sign In With Token Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void getBros(final String token, final BroCallback callback) {
        Log.d(TAG, "Get Bros: " + token);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Get Bro
                    GetBrosResponse getBrosResponse = null;
                    try {
                        getBrosResponse = (GetBrosResponse)sendApiMessage(GetBrosRequest.createMessage(token), new GetBrosResponse());

                        if(getBrosResponse != null) {

                            Log.d(TAG, "Get Bros Success: " + getBrosResponse.getSuccess());
                            if (getBrosResponse.getSuccess()) {
                                callback.onSuccess(getBrosResponse.getBros());
                            } else {
                                callback.onError(getBrosResponse.getError());
                            }
                        } else {
                            callback.onError("Get Bros Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void addBro(final String token, final String broName, final BroCallback callback) {
        Log.d(TAG, "Add Bro: " + token + ", " + broName);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Add Bro
                    GetBrosResponse getBrosResponse = null;
                    try {
                        getBrosResponse = (GetBrosResponse)sendApiMessage(AddBroRequest.createMessage(token, broName), new GetBrosResponse());

                        if(getBrosResponse != null) {

                            if (getBrosResponse.getSuccess()) {
                                callback.onSuccess(getBrosResponse.getBros());
                            } else {
                                callback.onError(getBrosResponse.getError());
                            }
                        } else {
                            callback.onError("Add Bro Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void removeBro(final String token, final String broName, final BroCallback callback) {
        Log.d(TAG, "Remove Bro: " + token + ", " + broName);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Remove Bro
                    GetBrosResponse getBrosResponse = null;
                    try {
                        getBrosResponse = (GetBrosResponse)sendApiMessage(RemoveBroRequest.createMessage(token, broName), new GetBrosResponse());

                        if(getBrosResponse != null) {

                            if (getBrosResponse.getSuccess()) {
                                callback.onSuccess(getBrosResponse.getBros());
                            } else {
                                callback.onError(getBrosResponse.getError());
                            }
                        } else {
                            callback.onError("Remove Bro Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void blockBro(final String token, final String broName, final BroCallback callback) {
        Log.d(TAG, "Block Bro: " + token + ", " + broName);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Block Bro
                    GetBrosResponse getBrosResponse = null;
                    try {
                        getBrosResponse = (GetBrosResponse)sendApiMessage(BlockBroRequest.createMessage(token, broName), new GetBrosResponse());

                        if(getBrosResponse != null) {

                            if (getBrosResponse.getSuccess()) {
                                callback.onSuccess(getBrosResponse.getBros());
                            } else {
                                callback.onError(getBrosResponse.getError());
                            }
                        } else {
                            callback.onError("Block Bro Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void onUpdateLocation(final String token, final BroLocation location, final UpdateCallback callback) {
        Log.d(TAG, "Update Location:  " + token + ", " + location.latitude + ", " + location.longitude);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Update Location
                    ErrorResponse errorResponse = null;
                    try {
                        errorResponse = (ErrorResponse)sendApiMessage(UpdateLocationRequest.createMessage(token, location), new ErrorResponse());

                        if(errorResponse != null) {

                            if (errorResponse.getSuccess()) {
                                callback.onSuccess();
                            } else {
                                callback.onError(errorResponse.getError());
                            }
                        } else {
                            callback.onError("Update Location Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void sendBroMessage(final String token, final String broName, final BroMessage message, final BroMessageCallback callback) {
        Log.d(TAG, "Send Bro Message: " + token + ", " + broName + ", " + message.messageTitle + ", " + message.messageDetails);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Block Bro
                    ErrorResponse sendBroMessage = null;
                    try {
                        sendBroMessage = (ErrorResponse)sendApiMessage(SendBroMessageRequest.createMessage(token, broName, message), new ErrorResponse());

                        if(sendBroMessage != null) {

                            if (sendBroMessage.getSuccess()) {
                                callback.onSuccessMessage();
                            } else {
                                callback.onError(sendBroMessage.getError());
                            }
                        } else {
                            callback.onError("Send Bro Message Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void getBroMessage(final String token, final String messageId, final BroMessageCallback callback) {
        Log.d(TAG, "Get Bro Message: " + token + ", " + messageId);
        try {
            new Thread(new Runnable() {
                public void run() {
                    // Do Block Bro
                    BroMessageResponse getBroMessage = null;
                    try {
                        getBroMessage = (BroMessageResponse)sendApiMessage(GetBroMessageRequest.createMessage(token, messageId), new BroMessageResponse());

                        if(getBroMessage != null) {

                            if (getBroMessage.getSuccess()) {
                                callback.onSuccess(getBroMessage.getBroMessage());
                            } else {
                                callback.onError(getBroMessage.getError());
                            }
                        } else {
                            callback.onError("Get Bro Message Failed");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError(e.getMessage());
                    }

                    networkCall.onComplete(NetworkServerApiCalls.this);
                }
            }).start();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private DataMessage sendApiMessage(byte[] message, DataMessage response) {

        Socket socket = null;

        try {

            socket = new Socket(host, port);

            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            //printByteArray(message);
            // Send Register Message
            outToServer.write(message);
            outToServer.flush();

            // Get response
            response.getBytesFromInput(inFromServer);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //printByteArray(response.getDataBytes());

        return response;
    }

    private String getGCMId() throws IOException {
        if(BroPreferences.getPrefs(BroApplication.getContext()).hasGCMId()) {
            return BroPreferences.getPrefs(BroApplication.getContext()).getGCMId();
        } else {
            InstanceID instanceID = InstanceID.getInstance(BroApplication.getContext());
            String token = instanceID.getToken(BroApplication.getContext().getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            BroPreferences.getPrefs(BroApplication.getContext()).setGCMId(token);
            return token;
        }
    }

    private void printByteArray(byte[] data) {

        if(data == null) {
            Log.d("Network", "Null!");
            return;
        }

        String hexString = "";

        for(int i=0;i<data.length;i++) {
            hexString += data[i] + " ";
        }

        Log.d("Network", hexString);
    }

}
