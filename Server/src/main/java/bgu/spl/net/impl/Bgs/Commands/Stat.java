package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.nio.charset.Charset;

public class Stat extends Command {
    private String userName;
    public Stat(byte[] bytes, int len, Charset utf8) {
        super((short)8);
        userName = new String(bytes,2,len-2,utf8);
    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.stat(userName,opCode);
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
