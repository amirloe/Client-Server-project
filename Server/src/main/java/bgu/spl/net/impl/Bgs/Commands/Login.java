package bgu.spl.net.impl.Bgs.Commands;

import bgu.spl.net.impl.Bgs.BidiMessagingProtocolImpl;

import java.nio.charset.Charset;

public class Login extends Command {
    private String userName;
    private String password;
    public Login(byte[] bytes, int len, Charset utf8) {
        super((short)2);
        InitUsernameAndPassword(bytes, len, utf8);
    }

    private void InitUsernameAndPassword(byte[] bytes, int len, Charset utf8) {
        setUserNameAndPass(bytes,len,utf8);
    }

    @Override
    public void execute(BidiMessagingProtocolImpl protocol) {
        protocol.login(userName,password,opCode);

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    private void setUserNameAndPass(byte[] bytes, int len, Charset utf8){
        int placeOfPassword=0;
        for(int i=2;i<len;i++){
            if((bytes[i]=='\0')){
                userName = new String(bytes,2,i-2,utf8);//Maybe Change.. need to debug
                placeOfPassword = i+1;
                password = new String (bytes,placeOfPassword,len-placeOfPassword,utf8);
                break;
            }
        }
    }

}
