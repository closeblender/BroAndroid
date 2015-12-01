package com.closestudios.bro.networking.requests;

import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class SignUpRequest {

    ServerRequest serverRequest;
    String broName;
    String password;

    public SignUpRequest(ServerRequest request) {
        serverRequest = request;

        byte[] data = new byte[serverRequest.getDataBytes().length];
        for(int i=0;i<data.length;i++) {
            data[i] = serverRequest.getDataBytes()[i];
        }

        ArrayList<byte[]> blocks = DataMessage.getBlocks(data);
        broName = new String (blocks.get(0));
        password = new String (blocks.get(1));

    }

    public String getBroName() {
        return broName;
    }

    public String getPassword() {
        return password;
    }

    public static byte[] createMessage(String broName, String password) throws IOException {

        ArrayList<byte[]> signUpBlocks = new ArrayList<>();

        signUpBlocks.add(broName.getBytes());
        signUpBlocks.add(password.getBytes());

        return ServerRequest.createMessage(DataMessage.createBlocks(signUpBlocks), ServerRequest.ServerRequestType.SignUp);
    }


}
