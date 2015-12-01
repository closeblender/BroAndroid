package com.closestudios.bro.networking.requests;

import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignInTokenRequest {


    ServerRequest serverRequest;
    String token;

    public SignInTokenRequest(ServerRequest request) {
        serverRequest = request;

        token = new String(serverRequest.getRequestBytes());

    }

    public String getToken() {
        return token;
    }

    public static byte[] createMessage(String token) throws IOException {
        return ServerRequest.createMessage(token.getBytes(), ServerRequest.ServerRequestType.SignInToken);
    }

}
