package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.Bgs.SocialDB;

import java.nio.charset.Charset;

public class PM extends Command {
    private String userName;
    private String contant;
    public PM(byte[] bytes, int len, Charset utf8) {
        super((short)6);
        InitUserNameAndContent(bytes, len, utf8);
    }

    private void InitUserNameAndContent(byte[] bytes, int len, Charset utf8) {
        int placeOfContent;
        for(int i = 2; i<=len; i++){
            if((bytes[i]=='\0')){
                userName = new String(bytes,2,i-2,utf8);//Maybe Change.. need to debug
                placeOfContent = i+1;
                contant = new String (bytes,placeOfContent,len-placeOfContent,utf8);
                break;
            }
        }
    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
    protocol.privateMessage(userName,contant,opCode);
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
