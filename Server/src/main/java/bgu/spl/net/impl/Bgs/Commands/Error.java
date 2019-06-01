package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.CommandEncDec;
import bgu.spl.net.impl.Bgs.SocialDB;

public class Error extends Command {
    //fields
    private short commandOpCode;

    public Error(short CommandOpCode){
        super((short)11);
        this.commandOpCode = CommandOpCode;

    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {

    }

    @Override
    public byte[] getBytes() {
        byte[] output = new byte[4];
        byte[] opBytes = CommandEncDec.shortToBytes(opCode);
        byte[] cmdBytes = CommandEncDec.shortToBytes(commandOpCode);
        output[0] =opBytes [0];
        output[1] = opBytes[1];
        output[2] = cmdBytes[0];
        output[3] = cmdBytes[1];
        return output;
    }
}
