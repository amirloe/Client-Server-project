package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.nio.charset.Charset;

public class Logout extends Command {
    public Logout(byte[] bytes, int len, Charset utf8) {
        super((short)3);
    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.logout(opCode);

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
