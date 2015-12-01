package com.closestudios.bro.networking.responses;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.BroMessage;
import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 12/1/15.
 */
public class BroMessageResponse extends ErrorResponse {

    public BroMessageResponse() {
        super();
    }

    public BroMessage getBroMessage() {
        return new BroMessage(getDataBytes());
    }

    public static byte[] createSuccessMessage(BroMessage broMessage) throws IOException {
        return createMessage(true, broMessage.getBytes());
    }



}
