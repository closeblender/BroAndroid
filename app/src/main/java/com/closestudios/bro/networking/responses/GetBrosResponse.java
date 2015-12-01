package com.closestudios.bro.networking.responses;

import com.closestudios.bro.networking.Bro;
import com.closestudios.bro.networking.DataMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by closestudios on 11/23/15.
 */
public class GetBrosResponse extends ErrorResponse {

    public GetBrosResponse() {
        super();
    }

    public Bro[] getBros() {

        ArrayList<byte[]> broBytes = DataMessage.getBlocks(getDataBytes());
        Bro[] bros = new Bro[broBytes.size()];
        for(int i=0;i<bros.length;i++) {
            bros[i] = new Bro(broBytes.get(i));
        }
        return bros;
    }

    public static byte[] createSuccessMessage(Bro[] bros) throws IOException {

        ArrayList<byte[]> broBlocks = new ArrayList<>();
        for(int i=0;i<bros.length;i++) {
            broBlocks.add(bros[i].getBytes());
        }

        byte[] broBytes = DataMessage.createBlocks(broBlocks);

        return createMessage(true, broBytes);
    }



}
