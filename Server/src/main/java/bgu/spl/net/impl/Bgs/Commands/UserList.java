package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.nio.charset.Charset;

public class UserList extends Command {
    public UserList(byte[] bytes, int len, Charset utf8) {
        super((short)7);
    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.userList(opCode);

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
