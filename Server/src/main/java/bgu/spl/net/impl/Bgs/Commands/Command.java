package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.lang.reflect.Array;
import java.nio.charset.Charset;

public abstract class Command {

    //fields
    protected short opCode;
    public Command(short opcode){
        this.opCode = opcode;
    }

    public abstract void execute(BidiMessagingProtocolImpl protocol);
    public abstract byte[] getBytes();



    /**
     * gets two arrayes of bytes and join them together.

     */

    protected int updateOutput(byte[] toAdd,byte[] output,Integer counter){
        for (byte b : toAdd) {
            output[counter] = b;
            counter++;
        }
        return counter;
    }

}
