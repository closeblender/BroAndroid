package com.closestudios.bro.networking.responses;

import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignInResponse extends ErrorResponse {

    public SignInResponse() {
        super();
    }

    public String getToken() {
        byte[] token = new byte[getDataBytes().length-1];
        for(int i=0;i<getDataBytes().length-1;i++) {
            token[i] = getDataBytes()[i+1];
        }
        return new String(token);
    }

    public static byte[] createSuccessMessage(String token) throws IOException {
        return createMessage(true, token.getBytes());
    }



}
